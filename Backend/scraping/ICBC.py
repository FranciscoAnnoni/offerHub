import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service as ChromeService 
from webdriver_manager.chrome import ChromeDriverManager 
import sys
sys.path.append('../')
import builder as builder
sys.path.append('../modelos')
from Promocion import Promocion
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains

# Configurar el driver de Selenium (en este caso, utilizaremos Chrome)
options = webdriver.ChromeOptions() 
options.add_argument('--headless')
options.add_argument("--window-size=1920,1200")
driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()), options=options)

# Navegar hasta la página de beneficios 


print("-----Inicio Scraping (ICBC)-----")
driver.get('https://www.beneficios.icbc.com.ar/promo')
#Buscar cuantos elementos tiene la pagina de beneficios

##cantElementos=5

#Funcion para scrollear hasta abajo de todo de la pagina de beneficios
last_height = driver.execute_script("return document.body.scrollHeight")

while False:##True:

   # Scroll down to the bottom.
   driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")

   # Wait to load the page.
   time.sleep(3)

   # Calculate new scroll height and compare with last scroll height.
   new_height = driver.execute_script("return document.body.scrollHeight")

   if new_height == last_height:

       break

   last_height = new_height

seccion_categorias = driver.find_elements(By.XPATH, '//a[contains(@class,"box-beneficio")]')
cantElementos = len(seccion_categorias)
print("Cantidad de Promociones: "+str(cantElementos))
#de la lista obtenida muestra link asociado y texto de cada elemento
for boton in seccion_categorias:
    elY=driver.execute_script('return document.querySelector(".ben-'+boton.get_attribute("data-id")+'").getBoundingClientRect().y')
    scY=driver.execute_script('return window.scrollY')
    if elY != scY:
        driver.execute_script('window.scrollTo(0, '+str(elY)+');')
    ##print(boton.get_attribute("data-id"))
    time.sleep(0.5)
    titulo=boton.find_element(By.XPATH, './/h3').get_attribute("innerHTML")
    url=boton.get_attribute("href")
    print("\t---Inicio Comercio "+titulo+"---")
    print("\t"+titulo)
    print("\tURL Promo: "+url)
    categoria=url.split("/")[4].replace("-"," ").title()
    print("\tCategoria: "+categoria)
    boton.click()
    time.sleep(2)
    logo=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_element(By.XPATH, './/div[contains(@class,"brand-logo")]').find_element(By.XPATH, './/img').get_attribute("src")
    print("\tURL Logo: "+logo)
    limits=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_elements(By.XPATH, './/time')
    vigencia= list(map(lambda x: x.get_attribute("innerHTML"), limits))
    #print("\tVigencia: " + vigencia)
    try:
        ##urlComercio=str(driver.execute_script('if(document.querySelector(".brand-link")){return document.querySelector(".brand-link").href}else{return ""}'))
        urlComercioBtn=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_element(By.XPATH, './/a[contains(@class,"brand-link")]')
        urlComercio=urlComercioBtn.get_attribute("href")
        print("\tURL Comercio: "+urlComercio)
    except NoSuchElementException:
        urlComercio=None
    try:
        descripcion=driver.find_element(By.XPATH, '//li[contains(@class,"description")]').get_attribute("innerHTML")
    except NoSuchElementException:
        descripcion=""
    print("\tDescripcion: "+descripcion)
    segmentos=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_elements(By.XPATH, './/div[contains(@class,"ticket-group-internal")]')
    tope=boton.find_element(By.XPATH, '//li[contains(@class,"tope")]').get_attribute("innerHTML")
    diasSemana = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
    actives=driver.execute_script('return Array.from(document.querySelectorAll(".days span")).map(function(e){ return e.classList[0];}).join()').split(",")
    dias=[]
    i=0
    print("\tDias: ")
    for active in actives:
        if active=="active":
            dias.append(diasSemana[i])
            print("\t\t"+diasSemana[i])
        i+=1
    print("\tTope: " + tope)
    print("\tTarjetas: ")
    tarjetas=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_element(By.XPATH, './/div[contains(@class,"accept")]').find_elements(By.XPATH, './/span')
    for tarjeta in tarjetas:
        nombreTarjeta=str(tarjeta.get_attribute("class")).replace("visad","Visa Debito").title()
        print("\t\t"+nombreTarjeta)
    tyc=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_element(By.XPATH, './/div[contains(@class,"legal")]').find_element(By.XPATH, './/p').get_attribute("innerHTML")
    print("\tTyC: " + tyc)
    print("\t\tSucursales: ")
    pcias=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_elements(By.XPATH, './/div[contains(@class,"filter-item")]')
    for pcia in pcias:
        nombre=pcia.find_element(By.XPATH, './/h4').text
        print("\t\tProvincia:"+nombre)
        pcia.click()
        sucs=pcia.find_elements(By.XPATH,'.//li')
        for suc in sucs:
            direccion=suc.text
            if len(direccion) >0:
                print("\t\t\tDireccion: "+direccion+" - Localidad: "+nombre)
    i=0
    for segmento in segmentos:
        promos=segmento.find_elements(By.XPATH, './/div[contains(@class,"ticket")]')
        for promo in promos:
            promocion = Promocion()

            if 'isClarificacion' not in promo.get_attribute('class').split():
                if 'div-descuento' not in promo.find_element(By.XPATH, './/div').get_attribute('class').split():
                    contenido=promo.find_element(By.XPATH, './/div[contains(@class,"content")]')
                    i+=1
                    print("\t\t---Promo "+str(i)+"---")
                    textoPromo=contenido.find_elements(By.XPATH, './/div')
                    tituloPromo= " ".join((" ".join(map(lambda x: x.get_attribute("innerText") if "footer" not in x.get_attribute("class").split() else "", textoPromo))).split())
                    print("\t\tTitulo: "+tituloPromo)
                    try:
                        nombreSegmento=contenido.find_element(By.XPATH, './/div[contains(@class,"footer")]').text
                        print("\t\tSegmento: "+nombreSegmento)
                    except NoSuchElementException:
                        nombreSegmento="Todos"
                        print("\t\tSegmento: "+nombreSegmento)
                    if 'ticket-payroll' in promo.get_attribute('class').split():
                        print("\t\tCondicion: SI DEPOSITÁS TU SUELDO EN ICBC")
            else:
                clarification=" ".join(promo.get_attribute("innerText").split())
                if len(clarification) > 0:
                    descripcion=descripcion+(" | " if len(descripcion) > 0 else "")+clarification
            promocion.titulo=titulo+": "+tituloPromo
            promocion.proveedor="ICBC"
            promocion.url=url
            promocion.tope=tope
            promocion.setearFecha("vigenciaDesde",vigencia[0])
            promocion.setearFecha("vigenciaHasta",vigencia[1])
            promocion.dias=dias
            promocion.tyc=tyc
            promocion.descripcion=descripcion
            promocion.guardar()
    driver.execute_script('return document.querySelector(".btn-close").click()')
    time.sleep(1)
    

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