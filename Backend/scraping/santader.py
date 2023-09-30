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
from normalizadorSantander import creadorDeTarjetas
from normalizadorSantander import setearTarjeta
from normalizadorSantander import asignadorCategoria
from Tarjeta import Tarjeta
from Entidad import Entidad
import utilidades as utilidades
from utilidades import obtenerCoordenadas
from Sucursal import Sucursal
import re

#config.setearEntorno()

# Configurar el driver de Selenium (en este caso, utilizaremos Chrome)
options = webdriver.ChromeOptions() 
options.add_argument('--headless')
options.add_argument("--window-size=1920,1200")
driver = webdriver.Chrome(options=options)

wait = WebDriverWait(driver, 900000)
#<a beneficioItem

# Navegar hasta la página de beneficios 
driver.get('https://www.santander.com.ar/banco/online/personas/beneficios/compras')

sleep(3)

navegador = wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"pager")]'))).find_element(By.XPATH, './/a[contains(@data-nav,"next")]')

entidad = Entidad()
entidad.nombre = "Santander"
entidad.tipo = "Bancaria"
idEntidad = entidad.guardar()

creadorDeTarjetas(idEntidad)

promocionesTotales = 0

while True:
    #Buscar cuantos elementos tiene la pagina de beneficios
    promociones = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//a[contains(@class,"beneficioItem")]')))
    cantidad = len(promociones)/2
    i=0
    for promo in promociones:
        if(i<cantidad):
            url=promo.get_attribute("href")
            urlImagen = promo.find_element(By.XPATH, './/img').get_attribute("src")

            driver.execute_script("window.open('');")
            driver.switch_to.window(driver.window_handles[1])
            driver.get(url)

            paginaRota = False

            try:
                testPaginaRota = driver.find_element(By.XPATH, './/div[contains(@class,"beneficioTitulo")]').find_element(By.XPATH, './/h1').text
            except NoSuchElementException:
                testPaginaRota = None
                paginaRota = True
            
            # Hay algunas promos que no estan disponibles, esto lo que hace es saltear las mismas para que no crashee
            if not paginaRota:
                nombreComercio = wait.until(EC.presence_of_element_located((By.XPATH, './/div[contains(@class,"beneficioTitulo")]'))).find_element(By.XPATH, './/h1').text

                subpromos = []

                categoria = asignadorCategoria(nombreComercio)

                print("\n\n\t--- Inicio Comercio "+ nombreComercio +" ---")
                print("\t\t Categoria: "+categoria)

                comercio = Comercio()
                comercio.nombre=nombreComercio
                comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
                comercio.logo=utilidades.imagenABase64(urlImagen)
                idComercio=comercio.guardar()

                condiciones = []      

                subpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, './/div[@class="beneficioBox"]')))

                #TRAIGO TYC        
                textosTYC = wait.until(EC.presence_of_element_located((By.XPATH, './/div[contains(@class,"legales")]'))).find_elements(By.XPATH, './/p')

                tyc = ""
                for texto in textosTYC:
                    tyc = tyc + texto.text


                print("\n\tInicio Promos:")

                p = 0

                ofertaAnterior = []
                cantidadPromos = 0
                listaVigencias = []
                listaDias = []
                listaTarjetas = []
                listaCondiciones = []
                listaTopes = []
                listaOfertas = []

                for subpromo in subpromos:
             
                    oferta = subpromo.find_element(By.XPATH, './/div[contains(@class,"titulo")]').find_element(By.XPATH, './/h2').text

                    if oferta not in ofertaAnterior:

                        ofertaAnterior.append(oferta)
                    
                        cancelada = False

                        try:    
                            tarjetasTexto = subpromo.find_element(By.XPATH, '//div[contains(@class,"aplica")]').find_elements(By.XPATH, './/li')
                        except NoSuchElementException:
                            cancelada = True
                            tarjetasTexto = None
                        
                        try:
                            superClub = subpromo.find_element(By.XPATH, '//div[contains(@class,"beneficioTitulo")]').find_element(By.XPATH, './/img[contains(@title,"SuperClub")]')
                        except NoSuchElementException:
                            superClub = None

                        if superClub: condiciones.append("Estar subscripto a SuperClub+")

                        try:
                            sorpresa = subpromo.find_element(By.XPATH, '//div[contains(@class,"beneficioTitulo")]').find_element(By.XPATH, './/p').text
                            if "Sorp" in sorpresa: condiciones.append("Estar subscripto a Sorpresa")
                        except NoSuchElementException:
                            sorpresa = None
                        
                        if not cancelada:

                            print("\n\t\t---Promo "+str(p+1)+"---")

                            print("\t\t  Oferta: "+oferta)

                            #TRAIGO VIGENCIA
                            vigenciaTexto = subpromo.find_element(By.XPATH, './/div[contains(@class,"vigencia")]').text[:-1]
                            print("\t\t  Vigencia: "+vigenciaTexto)

                            # Buscar todas las coincidencias de patrones de fecha en el texto
                            patron_fecha = re.compile(r'\d{2}/\d{2}/\d{4}')
                            fechas_encontradas = patron_fecha.findall(vigenciaTexto)

                            listaVigencias.append(fechas_encontradas)

                            #TRAIGO OFERTA
                            oferta = ""
                            ofertaSeparado = subpromo.find_element(By.XPATH, './/div[contains(@class,"titulo")]').find_elements(By.XPATH, './/h2')
                            if len(ofertaSeparado) == 1: oferta = ofertaSeparado[0].text
                            else:
                                for separado in ofertaSeparado:
                                    oferta = oferta + separado.text + " "
                            listaOfertas.append(oferta)

                            #TRAIGO REINTEGRO
                            reintegros = subpromo.find_element(By.XPATH, './/div[contains(@class,"titulo")]').find_elements(By.XPATH, './/p[contains(@dir,"ltr")]')
                            noHayReintegro = True
                            for reintegtoTexto in reintegros:
                                reintegro = reintegtoTexto.text
                                if "lido" in reintegro: sleep(4)
                                if "integro" in reintegro:
                                    noHayReintegro = False
                                    if "Sin" in reintegro:
                                        listaTopes.append("")
                                    else:
                                        listaTopes.append(reintegro)
                            
                            if noHayReintegro: listaTopes.append("") 

                            # TRAIGO DIAS DE LA SEMANA
                            diasSemana = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
                            diasDisponibles = []
                            elements = subpromo.find_element(By.XPATH, './/div[contains(@class,"semana")]').find_element(By.XPATH, './/div[contains(@class,"cell middle pos-2")]').find_elements(By.XPATH,'.//li')  
                            
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

                            listaDias.append(diasDisponibles)

                            #TRAIGO TARJETAS
                            print("\t\t  Tarjeta requerida: ")

                            tarjetas = []

                            for tarjetaTexto in tarjetasTexto:              
                                if "Contactless" in tarjetaTexto.text: condiciones.append("La Tarjeta debe ser Contactless")
                                if len(tarjetaTexto.text) != 0:
                                    tarjetasRepetidas = tarjetas
                                    for tarjeta in setearTarjeta(tarjetaTexto.text):
                                        print("\t\t\tTarjeta "+tarjeta.procesadora+" "+tarjeta.tipoTarjeta+" "+tarjeta.segmento)
                                        tarjetasRepetidas.append(tarjeta.guardar())
                                    tarjetas = list(set(tarjetasRepetidas))
                            
                            listaTarjetas.append(tarjetas)
                            
                            if len(tarjetas) == 0:
                                    print("NO CARGO NINGUNA TARJETA")
                                    sleep(20000)
                            
                            #MUESTRO TYC
                            print("\t\t  TyC: " + tyc)                       
                            
                            p+=1
                            promocionesTotales += 1
                            cantidadPromos +=1
                            listaCondiciones.append(condiciones)

                #TRAIGO SUCURSALES
                try:
                    driver.execute_script("arguments[0].click();", driver.find_element(By.XPATH, './/a[contains(@data-comp,"showLocalesAdheridos")]'))
                    sleep(0.5)
                    driver.execute_script("arguments[0].click();", driver.find_elements(By.XPATH, './/a[contains(@class,"bttn border")]')[1])

                    try:
                        sleep(0.5)
                        direcciones = driver.find_element(By.XPATH, './/div[contains(@class,"infoList")]').find_element(By.XPATH, './/ul').find_elements(By.XPATH, './/div[contains(@class,"info")]')

                    except NoSuchElementException:
                        direcciones = []

                except NoSuchElementException:
                        direcciones = []

                print("\t\t  Sucursales:")

                sucursales = []

                for direccion in direcciones:
                    direccionCompleta = direccion.find_element(By.XPATH, './/strong').text + ", " + direccion.find_element(By.XPATH, './/p').text

                    if "Cdad" in direccionCompleta: direccionCompleta = direccionCompleta.replace("Cdad", "Ciudad")

                    if "null" not in direccionCompleta:
                        sucursal = Sucursal()
                        sucursal.direccion = direccionCompleta
                        sucursales.append(sucursal.direccion)   
                        print("\t\t\t  " + direccionCompleta)
                

                contador = 0
                while cantidadPromos != contador:
                    promocion = Promocion()
                    promocion.topeTexto = listaTopes[contador]
                    promocion.topeNro = re.sub(r'<[^>]+>', '', listaTopes[contador])
                    promocion.titulo = nombreComercio+": "+ listaOfertas[contador]
                    promocion.setearTipoPromocion(listaOfertas[contador], listaTopes[contador])
                    numeros = promocion.obtenerPorcentajeYCantCuotas(listaOfertas[contador])
                    promocion.porcentaje=numeros["porcentaje"]
                    promocion.cuotas=numeros["cuotas"]
                    promocion.comercio = idComercio
                    promocion.condiciones = listaCondiciones[contador]
                    promocion.proveedor = idEntidad
                    promocion.tarjetas = listaTarjetas[contador]
                    promocion.url = url
                    promocion.setearFecha("vigenciaDesde", fechas_encontradas[0])
                    promocion.setearFecha("vigenciaHasta", fechas_encontradas[1])
                    promocion.setearCategoria(categoria)
                    promocion.dias = listaDias[contador]
                    promocion.sucursales = sucursales
                    promocion.tyc = tyc
                    promocion.guardar()
                    contador += 1

            driver.close()
            driver.switch_to.window(driver.window_handles[0])
            i +=1
        
    
    navegador = wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"pager")]'))).find_element(By.XPATH, './/a[contains(@data-nav,"next")]')
    if navegador.get_attribute("class") != "inactive":
        try:
            driver.execute_script("arguments[0].click();", navegador)
        except:
            break
    else:
        break

# Cerrar el navegador
driver.quit()
print(str(promocionesTotales) + " promociones cargadas correctamente.")



