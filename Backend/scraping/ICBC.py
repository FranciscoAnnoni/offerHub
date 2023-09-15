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
from Sucursal import Sucursal
import config as config
import re
import utilidades as utilidades
from Comercio import Comercio
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from Tarjeta import Tarjeta
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from Entidad import Entidad
from CategoriaPromocion import CategoriaPromocion

#config.setearEntorno()

# Configurar el driver de Selenium (en este caso, utilizaremos Chrome)
options = webdriver.ChromeOptions() 
options.add_argument('--headless')
options.add_argument("--window-size=1920,1200")
driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()), options=options)

# Navegar hasta la página de beneficios 
wait = WebDriverWait(driver, 900000)

print("-----Inicio Scraping (ICBC)-----")
driver.get('https://www.beneficios.icbc.com.ar/promo')
#Buscar cuantos elementos tiene la pagina de beneficios

entidad = Entidad()
entidad.nombre = "Banco ICBC"
entidad.tipo = "Bancaria"
entidad.telefono= "0810-444-4652 - Atención de lunes a viernes de 8 a 20 horas."
idEntidad = ""#entidad.guardar()

#Funcion para scrollear hasta abajo de todo de la pagina de beneficios
last_height = driver.execute_script("return document.body.scrollHeight")
i=0
while True:#i<2:#False:
   # Scroll down to the bottom.
   i=i+1
   driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")

   # Wait to load the page.
   time.sleep(3)

   # Calculate new scroll height and compare with last scroll height.
   new_height = driver.execute_script("return document.body.scrollHeight")

   if new_height == last_height:

       break

   last_height = new_height

seccion_categorias = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//a[contains(@class,"box-beneficio")]')))
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
    categoria=url.split("/")[4].replace("-"," ")
    print("\tCategoria: "+categoria)
    boton.click()
    time.sleep(2)
    try:
        logo=driver.find_element(By.XPATH, '//div[contains(@class,"detalle-beneficio")]').find_element(By.XPATH, './/div[contains(@class,"brand-logo")]').find_element(By.XPATH, './/img').get_attribute("src")
    except:
        logo = ""
    print("\tURL Logo: "+logo)
    limits=wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"detalle-beneficio")]'))).find_elements(By.XPATH, './/time')
    vigencia= list(map(lambda x: x.get_attribute("innerHTML"), limits))
    #print("\tVigencia: " + vigencia)
    try:
        ##urlComercio=str(driver.execute_script('if(document.querySelector(".brand-link")){return document.querySelector(".brand-link").href}else{return ""}'))
        urlComercioBtn=wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"detalle-beneficio")]'))).find_element(By.XPATH, './/a[contains(@class,"brand-link")]')
        urlComercio=urlComercioBtn.get_attribute("href")
        print("\tURL Comercio: "+urlComercio)
    except NoSuchElementException:
        urlComercio=None
    comercio = Comercio()
    comercio.nombre=titulo
    comercio.logo=utilidades.imagenABase64(logo)
    comercio.url=urlComercio
    comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
    idComercio=""#comercio.guardar()
    try:
        descripcion=driver.find_element(By.XPATH, '//li[contains(@class,"description")]').get_attribute("innerHTML")
    except NoSuchElementException:
        descripcion=""
    print("\tDescripcion: "+descripcion)
    segmentos=wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"detalle-beneficio")]'))).find_elements(By.XPATH, './/div[contains(@class,"ticket-group-internal")]')
    condiciones=[]
    try:
        exclusivoModo=wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"detalle-beneficio")]'))).find_element(By.XPATH, '//div[contains(@class,"exclusivo-modo")]')
        condiciones=["Exclusivo para pagos con MODO"]
    except NoSuchElementException:
        exclusivoModo=False

    if len(boton.find_element(By.XPATH, '//li[contains(@class,"tope")]').get_attribute("innerHTML")) != 0:    
        tope=boton.find_element(By.XPATH, '//li[contains(@class,"tope")]').get_attribute("innerHTML").split(":")[1].split("<")[0][1:]
    else:
        tope = ""
    print("TOPEEE: -"+tope+"-")

    dias = ["LU", "MA", "MI", "JU", "VI", "SA", "DO"]
    diasSemana = []
    actives=driver.execute_script('return Array.from(document.querySelectorAll(".days span")).map(function(e){ return e.classList[0];}).join()').split(",")
    i=0
    print(" ")
    for active in actives:
        if active=="active":
            if dias[i] == "LU":
                diasSemana.append("Lunes") 
            elif dias[i] == "MA":
                diasSemana.append("Martes")
            elif dias[i] == "MI":
                diasSemana.append("Miércoles")
            elif dias[i] == "JU":
                diasSemana.append("Jueves")
            elif dias[i] == "VI":
                diasSemana.append("Viernes")
            elif dias[i] == "SA":
                diasSemana.append("Sábado")
            elif dias[i] == "DO":
                diasSemana.append("Domingo")
        i+=1

    print("\tDias: ")
    for dia in diasSemana:
        print("\t\t"+dia)

    print("\tTope: " + tope)
    print("\tTarjetas: ")
    nombresTarjetas=[]
    tarjetas=wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"detalle-beneficio")]'))).find_element(By.XPATH, './/div[contains(@class,"accept")]').find_elements(By.XPATH, './/span')
    for tarjeta in tarjetas:
        nombreTarjeta=str(tarjeta.get_attribute("class")).replace("visad","Visa Debito").title()
        nombresTarjetas.append(nombreTarjeta)
        print("\t\t"+nombreTarjeta)
    tyc=wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"detalle-beneficio")]'))).find_element(By.XPATH, './/div[contains(@class,"legal")]').find_element(By.XPATH, './/p').get_attribute("innerHTML")
    print("\tTyC: " + tyc)
    print("\t\tSucursales: ")
    pcias=wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"detalle-beneficio")]'))).find_elements(By.XPATH, './/div[contains(@class,"filter-item")]')
    idsSucursales=[]
    for pcia in pcias:
        nombre=pcia.find_element(By.XPATH, './/h4').text
        print("\t\tProvincia:"+nombre)
        pcia.click()
        sucs=pcia.find_elements(By.XPATH,'.//li')
        for suc in sucs:
            direccion=suc.text
            if len(direccion) >0:
                print("\t\t\tDireccion: "+direccion+" - Localidad: "+nombre)
                sucursal= Sucursal()
                sucursal.direccion=direccion
                sucursal.idComercio=idComercio
                if nombre.lower() not in direccion.lower() and ("," not in direccion.lower() or "-" not in direccion.lower()):
                        direccion=direccion+" "+nombre.lower().replace("provincia de ","")
                sucursal.latitud,sucursal.longitud=utilidades.obtenerCoordenadas(direccion)
                if sucursal.latitud=="Error de geolocalización":
                    if nombre.lower() not in direccion.lower():
                        direccion=direccion+" "+nombre.lower().replace("provincia de ","")
                        sucursal.latitud,sucursal.longitud=utilidades.obtenerCoordenadas(direccion)
                sucursal.direccion = sucursal.direccion[2:]
                idsSucursales.append("")#sucursal.guardar())
    i=0
    for segmento in segmentos:
        promos=segmento.find_elements(By.XPATH, './/div[contains(@class,"ticket")]')
        for promo in promos:
            
            if 'isClarificacion' not in promo.get_attribute('class').split():
                if 'div-descuento' not in promo.find_element(By.XPATH, './/div').get_attribute('class').split():
                    promocion = Promocion()
                    promocionCondiciones=condiciones
                    idTarjetas=[]
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
                        nombreSegmento="Clasico"
                        print("\t\tSegmento: "+nombreSegmento)
                    for nombreTarjeta in nombresTarjetas:
                        tarjeta=Tarjeta()
                        tarjeta.entidad=idEntidad
                        tarjeta.segmento=nombreSegmento
                        if "Debito" in nombreTarjeta:
                            tarjeta.tipoTarjeta="Débito"
                        else: 
                            tarjeta.tipoTarjeta="Crédito"
                        tarjeta.procesadora=nombreTarjeta.replace(" Debito","").replace(" Credito","")
                        idTarjetas.append("")#tarjeta.guardar())
                    
                    if 'ticket-payroll' in promo.get_attribute('class').split():
                        promocionCondiciones.append("SI DEPOSITÁS TU SUELDO EN ICBC")
                        print("\t\tCondicion: SI DEPOSITÁS TU SUELDO EN ICBC")
                    promocion.titulo=titulo+": "+tituloPromo
                    
                    if "%" in tituloPromo or "descuento" in tituloPromo and tope.find("$") and tope != "":
                        promocion.topeTexto = tope
                        try:
                            promocion.topeNro = tope[tope.find("$") + 1:].split()[0]
                        except:
                            promocion.topeNro = ""
                        print("ÁAAAAAA "+promocion.topeNro)
                    else:
                        promocion.tope=""
                        promocion.topeNro=""

                    promocion.setearTipoPromocion(tituloPromo,tope)
                    numeros=promocion.obtenerPorcentajeYCantCuotas(tituloPromo)
                    promocion.porcentaje=numeros["porcentaje"]
                    print(promocion.porcentaje)
                    promocion.cuotas=numeros["cuotas"]
                    print(promocion.cuotas)
                    promocion.comercio=idComercio
                    print(promocion.comercio)
                    promocion.condiciones=promocionCondiciones
                    print(promocion.condiciones)
                    promocion.proveedor=idEntidad
                    print(promocion.proveedor)
                    promocion.tarjetas=idTarjetas
                    print(promocion.tarjetas)
                    promocion.url=url
                    print(promocion.url)
                    promocion.setearFecha("vigenciaDesde",vigencia[0])
                    promocion.setearFecha("vigenciaHasta",vigencia[1])
                    promocion.setearCategoria(categoria)
                    promocion.dias=diasSemana
                    print(promocion.dias)
                    promocion.sucursales=idsSucursales
                    print(promocion.sucursales)
                    promocion.tyc=tyc
                    print(promocion.tyc)
                    promocion.descripcion=descripcion
                    print(promocion.descripcion)

    driver.execute_script('return document.querySelector(".btn-close").click()')
    time.sleep(1)
    

# Cerrar el navegador
driver.quit()

