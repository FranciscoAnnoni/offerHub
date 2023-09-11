import time
import copy
import selenium
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service as ChromeService 
from webdriver_manager.chrome import ChromeDriverManager 
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException
from webdriver_manager.chrome import ChromeDriverManager 
import sys
sys.path.append('../')
import builder as builder 
sys.path.append('../modelos')
import config as config
from Promocion import Promocion
from selenium.webdriver.common.action_chains import ActionChains
from Comercio import Comercio
from CategoriaPromocion import CategoriaPromocion
from Tarjeta import Tarjeta
from Entidad import Entidad
import utilidades as utilidades
from utilidades import obtenerCoordenadas
from Sucursal import Sucursal


#config.setearEntorno()

# Configurar el driver de Selenium (en este caso, utilizaremos Chrome)
options = webdriver.ChromeOptions() 
options.add_argument('--headless')
options.add_argument("--window-size=1920,1200")
driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()), options=options)
wait = WebDriverWait(driver, 900000)
promocionesTotales = 0

# Navegar hasta la página de beneficios 
driver.get('https://club.lanacion.com.ar/beneficios')

print("-----Inicio Scraping (Club La Nación)-----")

entidad = Entidad()
entidad.nombre = "Club La Nación"
entidad.tipo = "Fidelidad"
idEntidad = entidad.guardar()

#Buscar cuantos elementos tiene la pagina de beneficios
cantElementos = int(driver.find_element(By.XPATH, '//strong').get_attribute("innerHTML"))
print(cantElementos)

#Funcion para scrollear hasta abajo de todo de la pagina de beneficios
i = 0
error = 0

while i < cantElementos:

    #obtiene todos los elementos con la clase pedida
    seccion_categorias = driver.find_elements(By.XPATH, '//div[contains(@class,"grid-item --col-8 --col-md-6 --col-lg-4 --col-xl-4")]')

    driver.execute_script("window.scrollBy(0, 9000)") # Esto es para corregir cuando se pasa scrolleando
    time.sleep(1)
    driver.execute_script("window.scrollBy(0, -500)") 
    time.sleep(2.5)

    i = len(seccion_categorias)



#de la lista obtenida muestra link asociado y texto de cada elemento
for boton in seccion_categorias:
    titulo = boton.find_element(By.XPATH, './/a').get_attribute("title")
    url = boton.find_element(By.XPATH, './/a').get_attribute("href")
    urlImagen = boton.find_element(By.XPATH, './/img').get_attribute("src")
    print("\n\n\t--- Inicio Comercio "+titulo+" ---")
    print("\t"+titulo)
    print("\tURL Promo: "+url)
    driver.execute_script("window.open('');")
    driver.switch_to.window(driver.window_handles[1])
    driver.get(url)
    time.sleep(2)

    

    categoria = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//a[contains(@class,"link club-link --primary --font-bold --font-xs")]')))[3].text
    # Esto lo hago por como está hecha la página
    condicion = "Otros" == categoria or "Tiendas" in categoria or "Transportes" in categoria or "cocina" in categoria or "gourmet" in categoria
    if condicion: categoria = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//a[contains(@class,"link club-link --primary --font-bold --font-xs")]')))[2].text
    
    print("\tCategoria: "+categoria)

    comercio = Comercio()
    comercio.nombre=titulo
    comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
    comercio.logo=utilidades.imagenABase64(urlImagen)
    idComercio=comercio.guardar()
    
    containersPromos= wait.until(EC.presence_of_all_elements_located((By.XPATH, '//article[contains(@class,"cln-ficha-card --roboto")]')))

    # TRAIGO SUCURSALES
    print("\t\t  Sucursales:")
    sucursales = []

    try:
        listaSucursales = driver.find_element(By.XPATH, '//div[contains(@class,"grid-item --col-xs-8 --col-md-12 --col-lg-4 --col-xl-5")]').find_elements(By.XPATH, './/span[contains(@class,"--twoxs")]')
        for direccionSucursal in listaSucursales:
            sucursal = Sucursal()
            sucursal.direccion = direccionSucursal.text
            latitud_resultado, longitud_resultado = obtenerCoordenadas(sucursal.direccion)
            sucursal.latitud = str(latitud_resultado)
            sucursal.longitud = str(longitud_resultado)
            sucursal.idComercio = idComercio
            sucursales.append(sucursal.guardar())
            print("\t\t\t  " + direccionSucursal.text) 

    except NoSuchElementException:
        listaSucursales = None
        print("\t\t  No cuenta con sucursales")
    
    print("\n\t\tInicio Promos:")
    a=0
    primeravez = True
    # Arranco a ver cada promo del local
    for containerPromo in containersPromos:
        a+=1
        print("\n\t\t---Promo "+str(a)+"---")
        oferta = containerPromo.find_element(By.XPATH, './/h2[contains(@class,"title --m")]').text        

        x=[i for i in oferta] 
        if "x" in x:
            print("\t\t  Beneficio: "+oferta)
        elif "%" in x:
            print("\t\t  Descuento: "+oferta)
        else: print("\t\t CHEQUEAR")

        try:
            print("\t\t  Descripción: " + containerPromo.find_element(By.XPATH, './/p[contains(@class,"paragraph --mb-24 --fourxs")]').text)
        except NoSuchElementException:
            None
        
        # TRAIGO LAS TARJETAS
        tarjetas = []
        tarjetasTexto = []
        textoTarjetas = "\t\t  Tarjeta requerida: "
        try:
            black = containerPromo.find_element(By.XPATH, './/span[contains(@class,"badge --mr-8 --black --small")]').text
              
            if len(black) > 0:
                tarjetaBlack = Tarjeta()
                tarjetaBlack.entidad = idEntidad
                tarjetaBlack.procesadora = "No posee"
                tarjetaBlack.segmento = "Black"
                tarjetaBlack.tipoTarjeta = "Fidelidad"
                
                tarjetas.append(tarjetaBlack.guardar())

                tarjetasTexto.append("Black")

        except NoSuchElementException:
            black = None

        try:
            premium = containerPromo.find_element(By.XPATH, './/span[contains(@class,"badge --mr-8 --premium --small")]').text
            tarjetasTexto.append(premium)

            tarjetaPremium = Tarjeta()
            tarjetaPremium.entidad = idEntidad
            tarjetaPremium.procesadora = "No posee"
            tarjetaPremium.segmento = "Premium"
            tarjetaPremium.tipoTarjeta = "Fidelidad"

            tarjetas.append(tarjetaPremium.guardar())
            
        except NoSuchElementException:
            premium = None

        try:
            classic = containerPromo.find_element(By.XPATH, './/span[contains(@class,"badge --mr-8 --classic --small")]').text
            tarjetasTexto.append(classic)

            tarjetaClassic = Tarjeta()
            tarjetaClassic.entidad = idEntidad
            tarjetaClassic.procesadora = "No posee"
            tarjetaClassic.segmento = "Classic"
            tarjetaClassic.tipoTarjeta = "Fidelidad"

            tarjetas.append(tarjetaClassic.guardar())
        except NoSuchElementException:
            classic = None

        for tarjeta in tarjetasTexto:
            textoTarjetas = textoTarjetas + tarjeta + ", "
        textoTarjetas = textoTarjetas[:-2]
        print(textoTarjetas)

        # TRAIGO LA VIGENCIA DE LA PROMOCION

        vigencia = containerPromo.find_element(By.XPATH, './/p[contains(@class,"paragraph --mr-16 --fourxs")]').text.split("|",1)

        print("\t\t  Vigencia: " + vigencia[0])

        vigenciaTexto = vigencia[0].split(" ")

        # TYC 
        tyc = ""
        driver.execute_script("arguments[0].scrollIntoView(true);", containerPromo.find_element(By.XPATH, './/div[contains(@class,"accordion card-footer")]').find_element(By.CLASS_NAME, value = "header"))
        time.sleep(1)
        driver.execute_script("window.scrollBy(0, -250)")
        time.sleep(1)
        containerPromo.find_element(By.XPATH, './/div[contains(@class,"accordion card-footer")]').find_element(By.CLASS_NAME, value = "header").click()     

        while len(tyc) == 0:           
            tyc = containerPromo.find_element(By.XPATH, './/div[contains(@class,"accordion card-footer --open")]').find_element(By.XPATH, './/p[contains(@class,"paragraph --fourxs")]').text
        
        print("\t\t  TyC: " + tyc)
        
           

        # TRAIGO DIAS DE LA SEMANA
        diasSemana = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
        diasDisponibles = []
        elements = containerPromo.find_element(By.XPATH, './/div[contains(@class,"week cln-week --mb-24")]').find_elements(By.XPATH, './/li[contains(@class,"day")]')
        
        contadorDia = 0
        for element in elements:
            dia = element.get_attribute("class")
            
            if len(dia) == 3:
                diasDisponibles.append(diasSemana[contadorDia])

            contadorDia += 1
           
            
        if len(diasDisponibles) == 7:
            print("\t\t  Días disponibles: Todos los días")
        else:
            texto = "\t\t  Días disponibles: "
            for dia in diasDisponibles:
                texto = texto + dia + ", "
            texto = texto[:-2]
            print(texto)
        
        promocionesTotales += 1

        promocion = Promocion()
        promocion.titulo = titulo + ": " + oferta
        promocion.setearTipoPromocion(oferta, "")
        numeros = promocion.obtenerPorcentajeYCantCuotas(oferta)
        promocion.porcentaje=numeros["porcentaje"]
        promocion.cuotas=numeros["cuotas"]
        promocion.comercio = idComercio
        promocion.topeTexto = ""
        promocion.topeNro = ""
        promocion.proveedor = idEntidad
        promocion.tarjetas = tarjetas
        promocion.condiciones = []
        promocion.url = url
        promocion.setearFecha("vigenciaDesde",vigenciaTexto[3])
        promocion.setearFecha("vigenciaHasta",vigenciaTexto[6])
        promocion.dias = diasDisponibles
        promocion.tyc = tyc
        promocion.sucursales = sucursales
        promocion.setearCategoria(categoria)
        promocion.guardar()     

    driver.close()
    driver.switch_to.window(driver.window_handles[0])
    

# Cerrar el navegador
driver.quit()
print(str(promocionesTotales) + " cargadas correctamente.")


#by facundin