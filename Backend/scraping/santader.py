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
#driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()), options=options)
driver = webdriver.Chrome()

wait = WebDriverWait(driver, 900000)
wait2 = WebDriverWait(driver, 5)

#<a beneficioItem

# Navegar hasta la página de beneficios 
driver.get('https://www.santander.com.ar/banco/online/personas/beneficios/compras')

sleep(1)

navegador = wait.until(EC.presence_of_element_located((By.XPATH, '//div[contains(@class,"pager")]'))).find_element(By.XPATH, './/a[contains(@data-nav,"next")]')

entidad = Entidad()
entidad.nombre = "Banco Santander"
entidad.tipo = "Bancaria"
#idEntidad = entidad.guardar()

todasTarjetas = []
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
            print(urlImagen)

            driver.execute_script("window.open('');")
            driver.switch_to.window(driver.window_handles[1])
            driver.get(url)

            paginaRota = False
            
            try:
                nombreComercio = driver.find_element(By.XPATH, './/div[contains(@class,"beneficioTitulo")]').find_element(By.XPATH, './/h1').text
            except NoSuchElementException:
                nombreComercio = None
                paginaRota = True
            
            # Hay algunas promos que no estan disponibles, esto lo que hace es saltear las mismas para que no crashee
            if not paginaRota:

                subpromos = []

                categoria = url.split("/")[5].replace("-"," ").title()

                print("\n\n\t--- Inicio Comercio "+ nombreComercio +" ---")
                print("\t\t Categoria: "+categoria)

                comercio = Comercio()
                comercio.nombre=nombreComercio
                comercio.categoria=CategoriaPromocion.obtenerCategoria(categoria)
                comercio.logo=utilidades.imagenABase64(urlImagen)
                #idComercio=comercio.guardar()

                condiciones = []      

                subpromos = wait.until(EC.presence_of_all_elements_located((By.XPATH, './/div[@class="beneficioBox"]')))
                print(len(subpromos))

                #TRAIGO TYC        
                tyc = wait.until(EC.presence_of_all_elements_located((By.XPATH, './/div[contains(@class,"legales")]')))[-1].text

                print("\n\t\tInicio Promos:")

                p = 0

                for subpromo in subpromos:
                    
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
                    
                    if not cancelada:

                        print("\n\t\t---Promo "+str(p+1)+"---")

                        #TRAIGO VIGENCIA
                        vigenciaTexto = subpromo.find_element(By.XPATH, './/div[contains(@class,"vigencia")]').text[:-1]
                        print("\t\t  Vigencia: "+vigenciaTexto)
                        vigencia = vigenciaTexto.split(" ")

                        #TRAIGO OFERTA
                        oferta = subpromo.find_element(By.XPATH, './/div[contains(@class,"titulo")]').text

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

                        #TRAIGO TARJETAS
                        print("\t\t  Tarjeta requerida: ")

                        for tarjetaTexto in tarjetasTexto:
                            #tarjeta = Tarjeta()
                            #tarjeta.entidad = idEntidad
                            print("\t\t\t"+tarjetaTexto.text)
                            if tarjetaTexto.text not in todasTarjetas: todasTarjetas.append(tarjetaTexto.text)
                        
                        #MUESTRO TYC
                        print("\t\t  TyC: " + tyc)

                        p+=1
                        promocionesTotales += 1



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

print(todasTarjetas)
# Cerrar el navegador
driver.quit()
print(str(promocionesTotales) + " cargadas correctamente.")
    