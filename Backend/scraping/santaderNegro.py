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

#config.setearEntorno()

# Configurar el driver de Selenium (en este caso, utilizaremos Chrome)
options = webdriver.ChromeOptions() 
options.add_argument('--headless')
options.add_argument("--window-size=1920,1200")
driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()), options=options)

#<a beneficioItem

# Navegar hasta la página de beneficios 
driver.get('https://www.santander.com.ar/banco/online/personas/beneficios/compras')

navegador = driver.find_element(By.XPATH, '//div[contains(@class,"pager")]').find_element(By.XPATH, './/a[contains(@data-nav,"next")]')

entidad = Entidad()
entidad.nombre = "Banco Santander"
entidad.tipo = "Bancaria"
idEntidad = entidad.guardar()

while True:
    #Buscar cuantos elementos tiene la pagina de beneficios
    promociones = driver.find_elements(By.XPATH, '//a[contains(@class,"beneficioItem")]')
    cantidad = len(promociones)/2
    print(cantidad)
    i=0
    for promo in promociones:
        if(i<cantidad):
            
    #    titulo=promo.find_element(By.XPATH, './/h1').text
            url=promo.get_attribute("href")
            urlImagen = promo.get_attribute("src")
            print(url)
            titleCajita = promo.get_attribute("data-title")
            try:
                driver.execute_script("window.open('');")
                driver.switch_to.window(driver.window_handles[1])
                driver.get(url)
                sleep(3)

                categoria=url.split("/")[5].replace("-"," ").title()
                print("\tCategoria: "+categoria)

                try:    
                    aplicaTarjeta=driver.find_element(By.XPATH, '//div[contains(@class,"aplica")]').text
                    print(aplicaTarjeta)
                except NoSuchElementException:
                    aplicaTarjeta=None


                nombreComercio = driver.find_element(By.XPATH, './/div[contains(@class,"beneficioTitulo")]').text
                print("\t---Inicio promocion "+nombreComercio+"---")

                comercio = Comercio()
                comercio.nombre=nombreComercio
                comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
                comercio.logo=utilidades.imagenABase64(urlImagen)
                idComercio=comercio.guardar()

                oferta = driver.find_element(By.XPATH, './/div[contains(@class,"titulo")]').text
                print("\t---Detalle promocion "+oferta+"---")

                vigenciaTexto = driver.find_element(By.XPATH, './/div[contains(@class,"vigencia")]').text[:-1]
                print("\t\t  Vigencia: "+vigenciaTexto)
                vigencia = vigenciaTexto.split(" ")

                legales = driver.find_element(By.XPATH, './/div[contains(@class,"legales")]').text
                print(legales)

                        # TRAIGO DIAS DE LA SEMANA
                diasSemana = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
                diasDisponibles = []
                elements = driver.find_element(By.XPATH, './/div[contains(@class,"semana")]').find_element(By.XPATH, './/div[contains(@class,"cell middle pos-2")]').find_elements(By.XPATH,'.//li')  
                
                contadorDia = 0
                for element in elements:
                    try:
                        dia = element.get_attribute("class")
                    except:
                        dia = ""

                    if dia == "active":
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

                promocion = Promocion()
                promocion.titulo=nombreComercio+": "+oferta
                promocion.proveedor=idEntidad
                promocion.comercio=idComercio
                promocion.condiciones = condiciones
                promocion.url=urlPromocion
                promocion.tarjetas = tarjetasDisponibles
                promocion.tope=reintegro
                promocion.setearFecha("vigenciaDesde",vigencia[3])
                promocion.setearFecha("vigenciaHasta",vigencia[6])
                promocion.dias=diasSemana
                promocion.tyc=tyc
                promocion.setearCategoria(categoria)
                promocion.guardar()

            except:
                print("no se pudo leer promocion de: " +titleCajita)


            driver.close()
            driver.switch_to.window(driver.window_handles[0])
            driver.implicitly_wait(5) 
        i +=1
    
    driver.implicitly_wait(10) 
    navegador = driver.find_element(By.XPATH, '//div[contains(@class,"pager")]').find_element(By.XPATH, './/a[contains(@data-nav,"next")]')
    if navegador.get_attribute("class") != "inactive":
        try:
            driver.execute_script("arguments[0].click();", navegador)
        except:
            break
    else:
        break


driver.quit()