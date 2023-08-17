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

# Navegar hasta la página de beneficios 
driver.get('https://beneficios.galicia.ar/')

sleep(2)
wait = WebDriverWait(driver, 900000)

print("-----Inicio Scraping (Banco Galicia)-----")

entidad = Entidad()
entidad.nombre = "Banco Galicia"
entidad.tipo = "Bancaria"
idEntidad = entidad.guardar()

#Buscar las categorías que haya
seccion_categorias = driver.find_element(By.XPATH, '//div[contains(@class,"sc-eYGnOm ihspQh")]').find_elements(By.XPATH, '//div[contains(@class,"sc-eVspGN dnkXOU brk-card brk-card-row pb-16 brk-card-click no-Hover sc-faSwKo bZRWcC")]')
seccion_categorias.append(driver.find_element(By.XPATH, '//div[contains(@class,"sc-eYGnOm ihspQh")]').find_element(By.XPATH, '//div[contains(@class,"sc-eVspGN dnkXOU brk-card brk-card-row pb-16 brk-card-click no-Hover sc-faSwKo gJIQup")]'))

i = 0
promocionesTotales = 0
#Este while hace que recorra todas las categorías
while i < len(seccion_categorias):
    seccion_categorias[i].click()
    cantidadTotalPromos = int(wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"sc-bdqyNK bLvEeo")]')))[0].text.split(" ")[1])
    cantidadPromosLeidas = 0
    cantidadPestañasLeidas = 0

    #Este while hace que recorra toda las promos de una categoria
    while cantidadPromosLeidas < cantidadTotalPromos:
        
        promosXPagina = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"jsx-856446149 brk-card-content")]')))   
        cantidadPromosLeidas = cantidadPromosLeidas + len(promosXPagina)
        o = 0

        #Este for es para entrar a todas las promos de la página
        while o < len(promosXPagina):
            promosXPagina = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"jsx-856446149 brk-card-content")]')))
            # TRAIGO IMAGEN COMERCIO
            urlImagen = promosXPagina[o].find_element(By.XPATH, '//div[contains(@class,"sc-dovKpQ gNNuPM")]').find_element(By.XPATH, '//img').get_attribute("src")
            promosXPagina[o].click()
            

            #Esto es para ver todas las subpromos de una promo
            subpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"jsx-309925303 grid-item-base grid-item--spacing px-0")]')))
            p = 0

            nombreYCategoria = wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@style,"min-height: 189px;")]')))
            nombreComercio = nombreYCategoria.find_element(By.XPATH, './/p').get_attribute("title")
            categoria = nombreYCategoria.find_elements(By.XPATH, './/p')[1].get_attribute("title")

            comercio = Comercio()
            comercio.nombre=nombreComercio
            comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
            comercio.logo=utilidades.imagenABase64(urlImagen)
            idComercio=comercio.guardar()
            

            print("\n\n\t--- Inicio Comercio "+ nombreComercio +" ---")
            print("\tCategoria: "+categoria)

            print("\n\t\tInicio Promos:")
            while p < len(subpromos):
                subpromos[p].click()
                condiciones = []     

                print("\n\t\t---Promo "+str(p+1)+"---")
                #print("\t\t  Descripción: " + _ ).text) NO TIENEN LAS DE GALICIA

                #TRAIGO VIGENCIA
                vigencia = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM bYVqER sc-eqJLUj fwCLcY")]')))[1].text.split(" ")

                #TRAIGO OFERTA               
                oferta = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"jsx-309925303 grid-item-base grid-item--spacing grid-item--xs-12")]')))[1].text

                reintegro = None
                
                if "%" in oferta and "cuotas" in oferta:
                        #A la hora de guardar las promos si las queremos separar lo hacemos x acá. Ahora no hace nada.
                        print("\t\t  Descuento: " + oferta)
                        print("\t\t  " + wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM bYVqER sc-eqJLUj fwCLcY")]')))[0].text)
                        reintegro = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM bYVqER sc-eqJLUj fwCLcY")]')))[0].text
                elif "cuotas" in oferta:
                        print("\t\t  Cuotas: " + oferta)
                elif "%" in oferta:
                        print("\t\t  Descuento: " + oferta)
                        print("\t\t  " + wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM bYVqER sc-eqJLUj fwCLcY")]')))[0].text)
                        reintegro = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM bYVqER sc-eqJLUj fwCLcY")]')))[0].text
                else: 
                     print("\t\t CHEQUEAR")
                     sleep(100000)
                     
                #Esto va acá adrede, no mover nada
                print("\t\t  Vigencia: " + wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM bYVqER sc-eqJLUj fwCLcY")]')))[1].text)

                urlPromocion = driver.current_url
                print("\tURL Promociones: "+urlPromocion)


                #TRAIGO DIAS SEMANA

                dias = driver.find_elements(By.XPATH, '//span[contains(@class,"sc-hLBbgP sc-gKPRtg jSwzrt evpxJq")]')
    
                diasSemana = []

                for dia in dias: diasSemana.append(dia.text) 
                texto = ""

                if len(diasSemana) == 7:
                    print("\t\t  Días disponibles: Todos los días")
                else:
                    texto = "\t\t  Días disponibles: "
                    for dia in diasSemana:
                        texto = texto + dia + ", "
                    texto = texto[:-2]
                    print(texto) 
                
                #TRAIGO TARJETAS
                print("\t\t  Tarjeta requerida: ")

                tarjetas = driver.find_elements(By.XPATH, '//div[contains(@class,"sc-cJRpLa fNeqqH")]')
                pagoEspecial = ""

                try:
                    pagosEspeciales = driver.find_elements(By.XPATH, '//span[contains(@class,"sc-hLBbgP sc-gKPRtg jSwzrt jWXhQs")]')
                    for pago in pagosEspeciales:
                         pagoEspecial = pagoEspecial + pago.text
                         
                except NoSuchElementException:
                    pagoEspecial = ""

                try:
                    pagosEspeciales = driver.find_elements(By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg bDrTM eIDMxy pb-0")]')
                    for pago in pagosEspeciales:
                         pagoEspecial = pagoEspecial + pago.text
                         
                except NoSuchElementException:
                    pagoEspecial = ""

                tarjetasDisponibles = []
                for tarjeta in tarjetas:

                    tarjetaDisponible = Tarjeta()
                    tarjetaDisponible.entidad = idEntidad                    
                    
                    nombreTarjeta = "\t\t\t  " + tarjeta.find_element(By.XPATH, './/img').get_attribute("alt")

                    procesadoras = ["American Express", "Mastercard", "Visa"]
                    segmentos = ["Eminent", "QR", "Contacto"]

                    if "Débito" in nombreTarjeta:
                        nombreTarjeta = "\t\t\t  Tarjeta Visa Débito"
                        tarjetaDisponible.procesadora = "Visa"
                        tarjetaDisponible.tipoTarjeta = "Débito"
                    else:
                         nombreTarjeta = nombreTarjeta + " Crédito"
                         tarjetaDisponible.tipoTarjeta = "Crédito"

                    for procesadora in procesadoras:
                         if procesadora in nombreTarjeta:
                              tarjetaDisponible.procesadora = procesadora
                    
                    if "Eminent" in pagoEspecial:
                        nombreTarjeta = nombreTarjeta + " Eminent"
                        tarjetaDisponible.segmento = "Eminent"
                    elif "QR" in pagoEspecial:
                        nombreTarjeta = nombreTarjeta + " (Pagando con QR)"
                        condiciones.append("Pago con QR")
                        tarjetaDisponible.segmento = "No posee"
                    elif "Contacto" in pagoEspecial:
                        nombreTarjeta = nombreTarjeta + " (Pagando Sin Contacto NFC)"
                        condiciones.append("Pago Sin Contacto NFC")
                        tarjetaDisponible.segmento = "No posee"
                    else:
                         tarjetaDisponible.segmento = "No posee"

                    print(nombreTarjeta)
                    tarjetasDisponibles.append(tarjetaDisponible.guardar())

                # TRAIGO SUCURSALES

                if "web" in driver.find_elements(By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM eIDMxy sc-eqJLUj fwCLcY")]')[0].text: print("\t\t  Dónde comprar: En el sitio web") #En algun momento hay que hacer que habra la pestaña nueva, agarre el link y lo guarde.
                else:
                    print("\t\t  Sucursales disponibles: ")
                    driver.find_element(By.XPATH, '//div[contains(@class,"MuiButtonBase-root MuiChip-root MuiChip-filled MuiChip-sizeMedium MuiChip-colorDefault MuiChip-clickable MuiChip-clickableColorDefault MuiChip-filledDefault sc-eSYpDc hcfWoM css-6yx8q4")]').click()
                    
                    cantidadTotalSucursales = int(wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"sc-bdqyNK bLvEeo")]'))).find_element(By.XPATH, './/p').text.split(" ")[1])
                    cantidadSucursalesLeidas = 0

                    while cantidadSucursalesLeidas < cantidadTotalSucursales:
                        sucursales = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"sc-leiOXd bQKZZO")]')))

                        for direccionSucursal in sucursales:
                            direccion = direccionSucursal.find_elements(By.XPATH, './/p')
                            print( "\t\t\t  " + direccion[0].text + ", " + direccion[1].text)
                            sucursal = Sucursal()
                            sucursal.direccion = direccion[0].text + ", " + direccion[1].text
                            latitud_resultado, longitud_resultado = obtenerCoordenadas(sucursal.direccion)
                            sucursal.latitud = str(latitud_resultado)
                            sucursal.longitud = str(longitud_resultado)
                            sucursal.idComercio = idComercio
                            sucursal.guardar()

                            
                            cantidadSucursalesLeidas += 1
                        
                        wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-hFnywE cTmfzt")]')))[1].click()


                    wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-jmpzUR cfetIB")]')))[1].click()

                botonesVolverASubpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-jmpzUR cfetIB")]')))
                
                #TRAIGO TYC 
                
                tyc = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg bDrTM eIDMxy")]')))[-1].text

                print("\t\t  TyC: " + tyc)

                #ESTO ESTÁ PORQUE A VECES METE UN ELEMENTO MAS A LA LISTA QUE NO EXISTE, CON ESTO LE DOY LA OPCIÓN DE HACERLO DENUEVO Y CORREGIRSE
                while len(botonesVolverASubpromos) != 2:
                     botonesVolverASubpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-jmpzUR cfetIB")]')))
                
                try:
                    botonesVolverASubpromos[1].click()
                except:
                    botonesVolverASubpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-jmpzUR cfetIB")]')))
                    botonesVolverASubpromos[1].click()

                subpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"jsx-309925303 grid-item-base grid-item--spacing px-0")]')))
                p += 1
                promocionesTotales += 1

                promocion = Promocion()
                promocion.titulo=nombreComercio+": "+oferta
                promocion.proveedor=idEntidad
                promocion.comercio=idComercio
                promocion.condiciones = condiciones
                promocion.url=urlPromocion
                promocion.tarjetas = tarjetasDisponibles
                promocion.tope=reintegro
                promocion.setearFecha("vigenciaDesde",vigencia[2])
                promocion.setearFecha("vigenciaHasta",vigencia[4])
                promocion.dias=diasSemana
                promocion.tyc=tyc
                promocion.setearCategoria(categoria)
                promocion.guardar()

            try:
                botonesVolverAPromos[1].click()
            except:
                botonesVolverAPromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-jmpzUR cfetIB")]')))
                botonesVolverAPromos[1].click()
    
            #Esto se hace por como anda la página de Galicia, cuando volves para atras después de ver una promoción te manda a la primera página. Con esto volvemos a la que estábamos
            for _ in range(cantidadPestañasLeidas):
                wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-hFnywE cTmfzt")]')))[1].click()

            sleep(1)
            o += 1

            

        #Paso a la siguiente pestaña de promociones de una misma categoria
        cantidadPestañasLeidas += 1
        wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-hFnywE cTmfzt")]')))[1].click()


    #con esto volvemos a las categorias, para pasar a la siguiente
    botones = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-jmpzUR cfetIB")]')))
    sleep(1)
    botones[0].find_element(By.XPATH, '//p[contains(@token,"[object Object]")]').click()
    sleep(1)
    seccion_categorias = driver.find_element(By.XPATH, '//div[contains(@class,"sc-eYGnOm ihspQh")]').find_elements(By.XPATH, '//div[contains(@class,"sc-eVspGN dnkXOU brk-card brk-card-row pb-16 brk-card-click no-Hover sc-faSwKo bZRWcC")]')
    seccion_categorias.append(driver.find_element(By.XPATH, '//div[contains(@class,"sc-eYGnOm ihspQh")]').find_element(By.XPATH, '//div[contains(@class,"sc-eVspGN dnkXOU brk-card brk-card-row pb-16 brk-card-click no-Hover sc-faSwKo gJIQup")]'))
    i+=1

# Cerrar el navegador
driver.quit()
print(str(promocionesTotales) + " cargadas correctamente.")


#by facundin
