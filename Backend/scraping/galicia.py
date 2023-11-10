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
import re


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
seccion_categorias = driver.find_element(By.XPATH, '//div[contains(@class,"sc-ibYzZP dlWflE")]').find_elements(By.XPATH, '//div[contains(@class,"sc-dkKxlM bqjISQ brk-card brk-card-row pb-16 brk-card-click no-Hover sc-hQZdRR kftZau")]')
seccion_categorias.append(driver.find_element(By.XPATH, '//div[contains(@class,"sc-ibYzZP dlWflE")]').find_element(By.XPATH, '//div[contains(@class,"sc-dkKxlM bqjISQ brk-card brk-card-row pb-16 brk-card-click no-Hover sc-hQZdRR dXwRJ")]'))

i = 0
promocionesTotales = 0
#Este while hace que recorra todas las categorías
while i < len(seccion_categorias):
    seccion_categorias[i].click()
    cantidadTotalPromos = int(wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"sc-fCBrnK hOebmf")]'))).text.split(" ")[1])
    cantidadPromosLeidas = 0
    cantidadPestañasLeidas = 0

    #Este while hace que recorra toda las promos de una categoria
    while cantidadPromosLeidas < cantidadTotalPromos:
            
            promosXPagina = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"jsx-3678252308 brk-card-content")]')))   
            cantidadPromosLeidas = cantidadPromosLeidas + len(promosXPagina)
            o = 0

            #Este for es para entrar a todas las promos de la página
            while o < len(promosXPagina):
                promosXPagina = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"jsx-3678252308 brk-card-content")]')))   
                # TRAIGO IMAGEN COMERCIO    
                nombreComercio = promosXPagina[o].find_element(By.XPATH, './/p[contains(@class,"sc-cNYriL sc-eThmLp jOlqBm PeEVI")]').text
                categoria = promosXPagina[o].find_element(By.XPATH, './/p[contains(@class,"sc-kDvujY sc-eDWCr gKuflY cdxmFY sc-kQvLVw byAUIa")]').text
                promosXPagina[o].click()

                if nombreComercio != "Farmacia Scienza":

                    #Esto es para ver todas las subpromos de una promo
                    subpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"sc-dkKxlM gSPZfe brk-card brk-card-row pb-16 brk-card-click no-Hover sc-iLmQsS ivzMHy")]')))
                    p = 0

                    urlImagen = driver.find_element(By.XPATH, '//div[contains(@class,"sc-fCYGkp jNmXJI")]').find_element(By.XPATH, '//img').get_attribute("src")

                    comercio = Comercio()
                    comercio.nombre=nombreComercio
                    comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
                    comercio.logo=utilidades.imagenABase64(urlImagen)
                    print(urlImagen)
                    #print(comercio.logo)
                    idComercio=comercio.guardar()
                    

                    print("\n\n\t--- Inicio Comercio "+ nombreComercio +" ---")
                    print("\tCategoria: "+categoria)

                    print("\n\t\tInicio Promos:")
                    while p < len(subpromos):
                        subpromos[p].click()
                        condiciones = []     

                        print("\n\t\t---Promo "+str(p+1)+"---")
                        #print("\t\t  Descripción: " + _ ).text) NO TIENEN LAS DE GALICIA
                        anda = True

                        sleep(1.5)
                        prueba = driver.find_elements(By.XPATH, '//p[contains(@class,"sc-kDvujY sc-eDWCr gKuflY kJDxkb sc-iOdfRm eNyMem")]')
                        
                        if len(prueba) == 0:
                            anda = False
                            print("ROMPIO LA PAGINA")
                        
                        if anda:
                            #TRAIGO VIGENCIA
                            vigencia = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-kDvujY sc-eDWCr gKuflY kJDxkb sc-iOdfRm eNyMem")]')))[1].text.split(" ")

                            #TRAIGO OFERTA               
                            oferta = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//h1[contains(@class,"sc-kDvujY sc-ipEyDJ jRMPgA bkESkY sc-bkldj cJbIlD")]')))[0].text

                            reintegro = ""
                            
                            if "%" in oferta:
                                    #A la hora de guardar las promos si las queremos separar lo hacemos x acá. Ahora no hace nada.
                                    print("\t\t  Descuento: " + oferta)
                                    #print("\t\t  " + wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-hLBbgP sc-gKPRtg GzUVM bYVqER sc-eqJLUj fwCLcY")]')))[0].text)
                                    reintegro = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-kDvujY sc-eDWCr gKuflY kJDxkb sc-iOdfRm eNyMem")]')))[0].text
                                
                            #Esto va acá adrede, no mover nada
                            print("\t\t  Vigencia: " + wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-kDvujY sc-eDWCr gKuflY kJDxkb sc-iOdfRm eNyMem")]')))[1].text)

                            urlPromocion = driver.current_url
                            print("\t\t  URL Promociones: "+urlPromocion)


                            #TRAIGO DIAS SEMANA

                            dias = driver.find_elements(By.XPATH, '//span[contains(@class,"sc-kDvujY sc-eDWCr doWjvI fgexGL")]')

                            if "los" not in dias[0].text:   
                                diasSemana = []

                                for dia in dias: diasSemana.append(dia.text) 
                                texto = "\t\t  Días disponibles: "
                                for dia in diasSemana:
                                    texto = texto + dia + ", "
                                texto = texto[:-2]
                                print(texto) 
                            else:
                                diasSemana = ["Lunes", "Martes", "Miércoles", "Jueves", "Sábado", "Domingo"]
                                print("\t\t  Días disponibles: Todos los días")
                            
                            #TRAIGO TARJETAS
                            print("\t\t  Tarjeta requerida: ")

                            tarjetas = driver.find_elements(By.XPATH, '//div[contains(@class,"sc-fpktUn dQgOHH")]')
                            pagoEspecial = ""

                            try:
                                pagosEspeciales = driver.find_elements(By.XPATH, '//span[contains(@class,"sc-kDvujY sc-eDWCr doWjvI bsyPIl")]')
                                for pago in pagosEspeciales:
                                    pagoEspecial = pagoEspecial + pago.text
                                    
                            except NoSuchElementException:
                                pagoEspecial = pagoEspecial

                            try:
                                pagosEspeciales = driver.find_elements(By.XPATH, '//p[contains(@class,"sc-kDvujY sc-eDWCr doWjvI gbxNow pb-0")]')
                                for pago in pagosEspeciales:
                                    pagoEspecial = pagoEspecial + pago.text
                                    
                            except NoSuchElementException:
                                pagoEspecial = pagoEspecial

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
                                
                                if "Éminent" in pagoEspecial or "Eminent" in pagoEspecial:
                                    nombreTarjeta = nombreTarjeta + " Éminent"
                                    tarjetaDisponible.segmento = "Éminent"
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

                            if "web" in driver.find_elements(By.XPATH, '//p[contains(@class,"sc-kDvujY sc-eDWCr gKuflY gbxNow sc-iOdfRm eNyMem")]')[0].text: print("\t\t  Dónde comprar: En el sitio web") 
                            else:
                                print("\t\t  Sucursales disponibles: ")
                                driver.find_element(By.XPATH, '//div[contains(@class,"MuiButtonBase-root MuiChip-root MuiChip-filled MuiChip-sizeMedium MuiChip-colorDefault MuiChip-clickable MuiChip-clickableColorDefault MuiChip-filledDefault sc-eFjyua eAIGAA css-6yx8q4")]').click()
                                
                                cantidadTotalSucursales = int(wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"sc-fCBrnK hOebmf")]'))).find_element(By.XPATH, './/p').text.split(" ")[1])
                                cantidadSucursalesLeidas = 0

                                while cantidadSucursalesLeidas < cantidadTotalSucursales:
                                    direcciones = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"sc-ibdxON irEaxf")]')))
                                    sucursales = []
                                    for direccionSucursal in direcciones:
                                        direccion = direccionSucursal.find_elements(By.XPATH, './/p')
                                        print( "\t\t\t  " + direccion[0].text + ", " + direccion[1].text)
                                        sucursal = Sucursal()
                                        sucursal.direccion = direccion[0].text + ", " + direccion[1].text
                                        sucursales.append(sucursal.direccion)

                                        
                                        cantidadSucursalesLeidas += 1
                                    
                                    wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-eCihoo iQpjyR")]')))[1].click()


                                wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))[1].click()

                            
                            #TRAIGO TYC 
                            
                            tyc = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//p[contains(@class,"sc-kDvujY sc-eDWCr doWjvI gbxNow")]')))[-1].text

                            print("\t\t  TyC: " + tyc)

                            

                            
                            promocionesTotales += 1

                            promocion = Promocion()
                            promocion.titulo=nombreComercio+": "+oferta
                            promocion.setearTipoPromocion(oferta, reintegro)
                            numeros = promocion.obtenerPorcentajeYCantCuotas(oferta)
                            promocion.porcentaje=numeros["porcentaje"]
                            promocion.cuotas=numeros["cuotas"]
                            promocion.proveedor=idEntidad
                            promocion.comercio=idComercio
                            promocion.condiciones = condiciones
                            promocion.url=urlPromocion
                            promocion.tarjetas = tarjetasDisponibles
                            promocion.topeTexto = reintegro
                            promocion.topeNro = re.sub(r'<[^>]+>', '', reintegro)
                            promocion.setearFecha("vigenciaDesde",vigencia[2])
                            promocion.setearFecha("vigenciaHasta",vigencia[4])
                            promocion.dias = diasSemana
                            promocion.sucursales = sucursales
                            promocion.tyc=tyc
                            promocion.setearCategoria(categoria)
                            promocion.guardar()

                            botonesVolverASubpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))

                            #ESTO ESTÁ PORQUE A VECES METE UN ELEMENTO MAS A LA LISTA QUE NO EXISTE, CON ESTO LE DOY LA OPCIÓN DE HACERLO DENUEVO Y CORREGIRSE
                            while len(botonesVolverASubpromos) != 2:
                                    botonesVolverASubpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))
                                
                            try:
                                botonesVolverASubpromos[1].click()
                            except:
                                botonesVolverASubpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))
                                botonesVolverASubpromos[1].click()

                            subpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//div[contains(@class,"sc-dkKxlM gSPZfe brk-card brk-card-row pb-16 brk-card-click no-Hover sc-iLmQsS ivzMHy")]')))
                            p += 1
                            
                        else:
                            try:
                                botonesVolverAPromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))
                                botonesVolverAPromos[1].click()
                                sleep(2)
                                p=1000
                            except:
                                botonesVolverAPromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))
                                botonesVolverAPromos[1].click()
                                sleep(2)
                                p=1000
                            

                try:
                    botonesVolverAPromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))
                    botonesVolverAPromos[1].click()
                except:
                    botonesVolverAPromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))
                    botonesVolverAPromos[1].click()
        
                #Esto se hace por como anda la página de Galicia, cuando volves para atras después de ver una promoción te manda a la primera página. Con esto volvemos a la que estábamos
                for _ in range(cantidadPestañasLeidas):
                    wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-eCihoo iQpjyR")]')))[1].click()

                sleep(1)
                o += 1

                

            #Paso a la siguiente pestaña de promociones de una misma categoria
            cantidadPestañasLeidas += 1
            wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-eCihoo iQpjyR")]')))[1].click()


    #con esto volvemos a las categorias, para pasar a la siguiente
    botones = wait.until(EC.presence_of_all_elements_located((By.XPATH, '//button[contains(@class,"sc-iFoMEM JEWKm")]')))
    sleep(1)
    botones[0].click()
    sleep(1)
    seccion_categorias = driver.find_element(By.XPATH, '//div[contains(@class,"sc-ibYzZP dlWflE")]').find_elements(By.XPATH, '//div[contains(@class,"sc-dkKxlM bqjISQ brk-card brk-card-row pb-16 brk-card-click no-Hover sc-hQZdRR kftZau")]')
    seccion_categorias.append(driver.find_element(By.XPATH, '//div[contains(@class,"sc-ibYzZP dlWflE")]').find_element(By.XPATH, '//div[contains(@class,"sc-dkKxlM bqjISQ brk-card brk-card-row pb-16 brk-card-click no-Hover sc-hQZdRR dXwRJ")]'))
    i+=1

# Cerrar el navegador
driver.quit()
print(str(promocionesTotales) + " cargadas correctamente.")


#by facundin
