from time import sleep
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service as ChromeService 
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


config.setearEntorno()

# Configurar el driver de Selenium (en este caso, utilizaremos Chrome)
options = webdriver.ChromeOptions() 
options.add_argument('--headless')
options.add_argument("--window-size=1920,1200")
driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()), options=options)
wait = WebDriverWait(driver, 900000)
#Si no abre en ventana completa rompe


# Navegar hasta la página de beneficios 
driver.get('https://www.bancociudad.com.ar/beneficios/')

wait = WebDriverWait(driver, 900000)
sleep(1)

print("-----Inicio Scraping (Banco Ciudad)-----")

entidad = Entidad()
entidad.nombre = "Banco Ciudad"
entidad.tipo = "Bancaria"
idEntidad = entidad.guardar()


#Buscar las categorías que haya
categorias = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"rubro-row")]')))
categorias = categorias[1:]
promocionesTotales = 0
sleep(1)
#Buscamos todas las categorías
for categoria in categorias:
    
    nombreCategoria = categoria.find_element(By.XPATH, './/span[contains(@class,"sidebar__rubro ng-binding")]').text

    driver.execute_script("arguments[0].click();", categoria)
    
    #Esto se hace para que carguen todas las promos de la página
    for i in range(20):
        sleep(0.2)
        driver.execute_script("window.scrollBy(0, 500)")
    
    promocionesXPagina = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"placa_beneficio_no_class card-beneficio col-xs-12 col-sm-6 col-md-6 col-lg-4 ng-scope")]')))

    for promocion in promocionesXPagina:
        condiciones = []
        driver.execute_script("arguments[0].scrollIntoView(true);", promocion)
        driver.execute_script("window.scrollBy(0, -200)") 
        sleep(0.2)
        promocion.click()
        urlPromocion = driver.current_url
        imagenes = driver.find_element(By.XPATH, '//div[contains(@class,"card-beneficio__logo detalle-logo")]').find_elements(By.XPATH, './/img')

        if len(imagenes[0].get_attribute("src")) == 0:
            urlImagen = imagenes[1].get_attribute("src")
        else:
            urlImagen = imagenes[0].get_attribute("src")

        nombreComercio = ""

        while len(nombreComercio) == 0:
            comercio = wait.until(EC.presence_of_all_elements_located((By.XPATH, './/div[contains(@class,"card-beneficio__comercio detalle-comercio ng-binding")]')))
            sleep(1)

            if len(comercio[0].text) == 0:
                    nombreComercio = comercio[1].text
            else:
                nombreComercio = comercio[0].text    
        
        print("\n\n\t--- Inicio Comercio "+nombreComercio+" ---")
        print("\tURL Promo: "+urlPromocion)
        print("\tCategoria: "+nombreCategoria)
        
        #TRAIGO OFERTA               
        oferta = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//span[contains(@class,"card-beneficio__label detalle-descuento ng-binding")]')))[0].text

        if len(oferta) < 3:
                oferta = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//span[contains(@class,"card-beneficio__label detalle-descuento ng-binding")]')))[2].text
        if len(oferta) < 3:
                oferta = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//span[contains(@class,"card-beneficio__label detalle-descuento ng-binding")]')))[1].text
        if len(oferta) < 3:
                oferta = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//span[contains(@class,"card-beneficio__label detalle-descuento ng-binding")]')))[3].text

        if "%" in oferta:
                        print("\tDescuento: " + oferta)
        elif "cuotas" in oferta:
                        print("\tCuotas: " + oferta)
        elif "x" in oferta:
                        print("\tPromocion: " + oferta)
        else: 
                    print("\tPromocion: 2x1")

        #TRAIGO VIGENCIA
        vigencia = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//h6[contains(@class,"legales__titulo ng-binding")]')))[0].text
        if len(vigencia) < 3:
            vigencia = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//h6[contains(@class,"legales__titulo ng-binding")]')))[1].text

        print("\tVigencia: " + vigencia)

        vigenciaTexto = vigencia.split(" ")

        #TRAIGO DIAS SEMANA

        dias = driver.find_elements(By.XPATH, '//span[contains(@ng-class,"beneficioDetalle.dias")]')
        dias = dias[:7]

        diasSemana = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
        diasDisponibles = []
        
        contadorDia = 0      

        for dia in dias:
            a = dia.get_attribute("class")
        
            if len(a) != 0:
                diasDisponibles.append(diasSemana[contadorDia])

            contadorDia += 1

        texto = "\tDías disponibles: "
        for dia in diasDisponibles:
            texto = texto + dia + ", "
        texto = texto[:-2]

        if texto == "\tDías disponibles":
            print("\tDías disponibles: Todos los días")
        else:
            print(texto)

        #TRAIGO TARJETAS
        print("\tTarjeta requerida: ")

        distribuidorasDePago = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@style,"display: flex;")]')))
        tarjetasRequeridas = distribuidorasDePago[0].find_elements(By.XPATH, './/img[contains(@ng-src,"/beneficios_rest/beneficios/tarjeta/")]')
        if len(tarjetasRequeridas) == 0: tarjetasRequeridas = distribuidorasDePago[1].find_elements(By.XPATH, './/img[contains(@ng-src,"/beneficios_rest/beneficios/tarjeta/")]')
        tarjetas = []

        noEsModo = True
        for tarjetaRequerida in tarjetasRequeridas:
            
            distribuidora = tarjetaRequerida.get_attribute("alt")

            tarjeta = Tarjeta()
            tarjeta.entidad = idEntidad
            tarjeta.segmento = "No posee"
            tarjeta.tipoTarjeta = "Crédito"

            if distribuidora == "VISA":
                    print("\t\t Tarjeta Banco Ciudad Visa")
                    tarjeta.procesadora = "Visa"
                    tarjetas.append(tarjeta.guardar())
            elif distribuidora == "MASTERCARD":
                    print("\t\t Tarjeta Banco Ciudad Mastercard")
                    tarjeta.procesadora = "Mastercard"
                    tarjetas.append(tarjeta.guardar())
            elif distribuidora == "CABAL":
                    print("\t\t Tarjeta Banco Ciudad Cabal")
                    tarjeta.procesadora = "Cabal"
                    tarjetas.append(tarjeta.guardar())
            elif distribuidora == "VISA DEBITO":
                    print("\t\t Tarjeta Banco Ciudad Visa Débito")
                    tarjeta.tipoTarjeta = "Débito"
                    tarjeta.procesadora = "Visa"
                    tarjetas.append(tarjeta.guardar())
            elif distribuidora == "MAESTRO":
                    print("\t\t Tarjeta Banco Ciudad Maestro")
                    tarjeta.procesadora = "Maestro"
                    tarjetas.append(tarjeta.guardar())
            elif distribuidora == "MODO":
                    condiciones.append("Pagando a través de MODO")
                    if len(tarjetasRequeridas) == 1:
                        noEsModo = False
            else: sleep(10000)             
        
        # GUARDO EL COMERCIO
        if noEsModo:
            comercio = Comercio()
            comercio.nombre=nombreComercio
            comercio.categoria=CategoriaPromocion.obtenerCategoria(nombreCategoria)
            comercio.logo=utilidades.imagenABase64(urlImagen)
            idComercio=comercio.guardar()

            # TRAIGO SUCURSALES
            print("\tSucursales disponibles: ")
            direcciones = wait.until(EC.presence_of_element_located((By.XPATH, '//ul[contains(@id,"lista-sucursales-detalle")]'))).find_elements(By.XPATH, './/li[contains(@ng-repeat,"sucursal in sucursalesCercanas")]')

            if len(direcciones) == 0: print("\t\tNo hay sucursales especificadas")
            
            for direccion in direcciones:
                sucursal = Sucursal()
                ciudad = direccion.find_element(By.XPATH, './/p[@class="ng-binding"]').text.split(", ")
                if len(ciudad) == 2: sucursal.direccion = direccion.find_element(By.XPATH, './/p[contains(@class,"ng-binding")]').text+", "+ciudad[1]
                else: sucursal.direccion = direccion.find_element(By.XPATH, './/p[contains(@class,"ng-binding")]').text+", "+ciudad[0]   
                latitud_resultado, longitud_resultado = obtenerCoordenadas(sucursal.direccion)
                sucursal.latitud = str(latitud_resultado)
                sucursal.longitud = str(longitud_resultado)
                sucursal.idComercio = idComercio
                sucursal.guardar()
                print("\t\t"+sucursal.direccion)

            #TRAIGO TYC

            tyc = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"legales col-xs-12")]')))[0].find_element(By.XPATH, './/p[contains(@ng-bind-html,"beneficioDetalle.legales")]').text
            
            if len(tyc) == 0: tyc = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"legales col-xs-12")]')))[1].find_element(By.XPATH, './/p[contains(@ng-bind-html,"beneficioDetalle.legales")]').text
            
            print("\t\t  TyC: " + tyc)

            if len(tyc) == 0: sleep(80000)
        
            promocion = Promocion()
            promocion.proveedor=idEntidad
            promocion.titulo = nombreComercio+": "+oferta
            promocion.proveedor = "Banco Ciudad"
            promocion.comercio=idComercio
            promocion.url = urlPromocion
            promocion.tarjetas = tarjetas
            promocion.setearFecha("vigenciaDesde",vigenciaTexto[4])
            promocion.setearFecha("vigenciaHasta",vigenciaTexto[7])
            promocion.dias = diasDisponibles
            promocion.tyc = tyc
            promocion.setearCategoria(nombreCategoria)
            promocion.condiciones = condiciones
            promocion.guardar()
            promocionesTotales += 1

        #Con esto se vuelve a las promociones
        botonVuelta = []
        while len(botonVuelta) != 2:
            botonVuelta = driver.find_elements(By.XPATH, '//i[contains(@class,"modal__boton-cerrar icon-bcba_general_arrow_l")]')

        if "Vol" in botonVuelta[0].find_element(By.XPATH, './/span').text:
            botonVuelta[0].click()
        else:
            botonVuelta[1].click()
        
    wait.until(EC.presence_of_element_located((By.XPATH, '//label[contains(@class,"limpiar-filtros")]'))).click()

# Cerrar el navegador
driver.quit()
print(str(promocionesTotales) + " cargadas correctamente.")

#by facundin