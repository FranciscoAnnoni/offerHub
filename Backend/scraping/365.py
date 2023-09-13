import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service as ChromeService 
from webdriver_manager.chrome import ChromeDriverManager 
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
import sys
sys.path.append('../')
import builder as builder
sys.path.append('../modelos')
import config as config
import utilidades as utilidades
from Tarjeta import Tarjeta
from Comercio import Comercio
from Entidad import Entidad
from Sucursal import Sucursal
from CategoriaPromocion import CategoriaPromocion
from Promocion import Promocion

#config.setearEntorno()

# Configurar el driver de Selenium (en este caso, utilizaremos Chrome)
options = webdriver.ChromeOptions() 
options.add_argument('--headless')
options.add_argument("--window-size=1920,1200")
driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()), options=options)

# Navegar hasta la página de beneficios 


print("-----Inicio Scraping (Clarin 365)-----")
entidad = Entidad()
entidad.nombre = "Clarin 365"
entidad.tipo = "Fidelidad"
entidad.telefono= "0810-333-0365 - Lunes a Viernes - 8 a 20hs"
idEntidad = entidad.guardar()

driver.get('https://365.clarin.com/buscar')
#Buscar cuantos elementos tiene la pagina de beneficios
cantElementos = int(driver.find_element(By.XPATH, '//div[contains(@id,"clarin365mostrando")]').find_element(By.XPATH, '//strong').text)
print("Cantidad de Promociones: "+str(cantElementos))

#Funcion para scrollear hasta abajo de todo de la pagina de beneficios
i = 0
while i < cantElementos:

    #obtiene todos los elementos con la clase pedida
    seccion_categorias = driver.find_elements(By.XPATH, '//a[contains(@class,"clarin-365-card")]')

    i = len(seccion_categorias)

    driver.execute_script("window.scrollBy(0, 1000)")
    time.sleep(3)
seccion_categorias = driver.find_elements(By.XPATH, '//a[contains(@class,"clarin-365-card")]')
#de la lista obtenida muestra link asociado y texto de cada elemento
for boton in seccion_categorias:
    titulo=boton.find_element(By.XPATH, './/h1').text
    url=boton.get_attribute("href")
    print("\t---Inicio Comercio "+titulo+"---")
    print("\t"+titulo)
    print("\tURL Promo: "+url)
    categoria=url.split("/")[3].replace("-"," ").title()
    print("\tCategoria: "+categoria)
    driver.execute_script("window.open('');")
    driver.switch_to.window(driver.window_handles[1])
    driver.get(url)
    time.sleep(3)
    try:
        urlComercioBtn=driver.find_element(By.XPATH, '//a[contains(@class,"website-link-wrapper")]')
        urlComercio=urlComercioBtn.get_attribute("href")
        print("\t\tURL Comercio: "+urlComercio)
    except NoSuchElementException:
        urlComercio=None
    comercio = Comercio()
    comercio.nombre=titulo
    comercio.url=urlComercio
    comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
    idComercio=comercio.guardar()
    containersPromos=driver.find_elements(By.XPATH, '//div[contains(@class,"clarin-ficha-beneficio")]')
    print("\t\tInicio Promos:")
    i=0
    for containerPromo in containersPromos:
        i+=1
        promocion = Promocion()
        print("\t\t---Promo "+str(i)+"---")
        porcentaje=containerPromo.find_element(By.XPATH, './/div[contains(@class,"benefit")]').get_attribute("textContent")
        print("\t\tPorcentaje: "+porcentaje)
        descripcion=containerPromo.find_element(By.XPATH, './/div[contains(@class,"description")]').get_attribute("textContent")
        print("\t\tDescripcion: "+descripcion)
        diasSemana = ["LU", "MA", "MI", "JU", "VI", "SA", "DO"]
        dias=[]
        i=0
        print("\tDias: ")
        diasContainer=containerPromo.find_element(By.XPATH, './/div[contains(@class,"apply-days-wrapper")]')
        dias=diasContainer.find_elements(By.XPATH, './/li[contains(@class,"active")]')
        if len(dias)>0:
            dias=list(sorted(map(lambda el:el.text,dias),key=diasSemana.index()))
        else:
            dias=list(diasSemana)
        for dia in dias:
            print("\t\t"+dia)
        tarjetas=containerPromo.find_elements(By.XPATH, './/div[contains(@class,"card-365")]')
        idTarjetas=[]
        print("\t\tTarjetas: ")
        for tarjeta in tarjetas:
            nombreTajeta=tarjeta.find_element(By.XPATH, './/p').get_attribute("innerHTML")
            print("\t\t\t"+nombreTajeta) 
            tarjetaNueva = Tarjeta()
            tarjetaNueva.entidad = idEntidad
            tarjetaNueva.procesadora = "No posee"
            tarjetaNueva.segmento = nombreTajeta
            tarjetaNueva.tipoTarjeta = "Fidelidad"
            idTarjetas.append(tarjetaNueva.guardar())
        tycContainer=containerPromo.find_element(By.XPATH, './/div[contains(@class,"terms-and-conditions-wrapper")]')  
        vigencia=tycContainer.find_element(By.XPATH, './/p').text.replace("Válido hasta el ","")
        print("\t\tVigencia: "+vigencia)
        tycBtn=tycContainer.find_element(By.XPATH, './/button')
        driver.implicitly_wait(2)
        ActionChains(driver).move_to_element(tycBtn).click(tycBtn).perform()
        time.sleep(1)
        tyc=driver.find_element(By.XPATH, '//div[contains(@class,"terminos-modal")]').find_element(By.XPATH, './/p').text
        print("\t\tT y C: "+tyc)
        driver.execute_script('document.querySelector(".scrollable").remove()')
        idSucursales=[]
        try:
            print("\t\tSucursales: ")
            sucBtn=tycContainer.find_element(By.XPATH, './/button[contains(@class,"sucursales-adheridas")]')
            driver.implicitly_wait(2)
            ActionChains(driver).move_to_element(sucBtn).click(sucBtn).perform()
            btns=driver.find_elements(By.XPATH, '//div[contains(@class,"benefit-offices-icon")]')
            for btn in btns:
                btn.click()
##            driver.execute_script('document.querySelectorAll(".benefit-offices-icon").forEach(function(a){a.click();})')
            pcias=driver.find_elements(By.XPATH, '//ul[contains(@class,"benefitOffices")]')
            for pcia in pcias:
                nombrePcia=pcia.find_element(By.XPATH, './/h1').text
                print("\t\tProvincia:"+nombrePcia)
                sucs=pcia.find_element(By.XPATH, './/ul[contains(@class,"provinceBenefitOffices")]').find_elements(By.XPATH, './/li')
                for suc in sucs:
                    direccion=suc.find_element(By.XPATH, './/h2').text
                    localidad=suc.find_element(By.XPATH, './/h3').text
                    sucursal = Sucursal()
                    sucursal.direccion = direccion+" "+localidad
                    sucursal.latitud, sucursal.longitud = utilidades.obtenerCoordenadas(sucursal.direccion)
                    if sucursal.latitud=="Error de geolocalización":
                        if nombrePcia.lower() not in direccion.lower():
                            direccion=sucursal.direccion+" "+nombrePcia.lower().replace("provincia de ","")
                            sucursal.latitud,sucursal.longitud=utilidades.obtenerCoordenadas(direccion)
                    sucursal.idComercio = idComercio
                    idSucursales.append(sucursal.guardar())
                    print("\t\t\tDireccion: "+direccion+" - Localidad: "+localidad)
            driver.execute_script('document.querySelector(".scrollable").remove()')
        except NoSuchElementException:
            sucursales=None 
        promocion.titulo=titulo+": "+porcentaje
        if len(idSucursales) > 0:
            promocion.sucursales=idSucursales
        promocion.setearTipoPromocion(porcentaje)
        numeros=promocion.obtenerPorcentajeYCantCuotas(porcentaje)
        promocion.porcentaje=numeros["porcentaje"]
        promocion.tarjetas=idTarjetas
        promocion.proveedor=idEntidad
        promocion.comercio=idComercio
        promocion.topeTexto = ""
        promocion.topeNro = ""
        promocion.url=url
        if vigencia:
            promocion.vigenciaDesde=" - "
            promocion.setearFecha("vigenciaHasta",vigencia)
        else:
            promocion.vigenciaDesde=" - "
            promocion.vigenciaHasta=" - "
        promocion.setearCategoria(categoria)
        promocion.dias=dias
        promocion.tyc=tyc
        promocion.descripcion=descripcion
        promocion.guardar()

        #driver.find_element(By.XPATH,"//html").click()

    driver.close()
    driver.switch_to.window(driver.window_handles[0])


# Cerrar el navegador
driver.quit()

"""
# Iterar sobre las categorías y obtener la información de las promociones
for categoria in categorias:
    # Obtener el nombre de la categoría
    nombre_categoria = categoria.find_element(By.CSS_SELECTOR, '.beneficios-item__title').text
    print('Categoría:', nombre_categoria)

    # Hacer clic en la categoría para mostrar las promociones
    categoria.click()

    # Esperar a que se carguen las promociones
    driver.implicitly_wait(5)  # Esperar 5 segundos (puedes ajustar este valor según sea necesario)

    # Encontrar las promociones de la categoría
    promociones = driver.find_elements(By.CSS_SELECTOR, '.beneficios-landing__item')

    # Iterar sobre las promociones y obtener la información deseada
    for promocion in promociones:
        titulo = promocion.find_element(By.CSS_SELECTOR, '.beneficios-landing__title').text
        descripcion = promocion.find_element(By.CSS_SELECTOR, '.beneficios-landing__description').text

        print('Título:', titulo)
        print('Descripción:', descripcion)
        print('---')

    # Regresar a la página de todas las categorías
    boton_volver = driver.find_element(By.XPATH, '//a[text()="Volver"]')
    boton_volver.click()

# Cerrar el navegador
driver.quit()
"""