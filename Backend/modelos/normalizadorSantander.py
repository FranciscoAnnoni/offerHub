from Tarjeta import Tarjeta
from time import sleep

tarjetas = []

def creadorDeTarjetas(idEntidad):
    
    o = 0

    procesadoras = ["Visa", "Mastercard", "American Express", "Débito"]

    segmentos = ["Black", "Platinum", "Gold", "Infinity", "Clásico", "Nova", "Women", "Recargable"]

    for segmento in segmentos:
        for procesadora in procesadoras:
            o+=1
            tarjeta = Tarjeta()
            tarjeta.segmento = segmento
            tarjeta.entidad = idEntidad
            if procesadora == "Débito":
                tarjeta.procesadora = "Visa"
                tarjeta.tipoTarjeta = "Débito"
            else:
                tarjeta.procesadora = procesadora
                tarjeta.tipoTarjeta = "Crédito"
            tarjetas.append(tarjeta)

def filtrarPorSegmento(lista, texto):
    tarjetas = []

    for tarjeta in lista:
        if tarjeta.segmento == texto:
            tarjetas.append(tarjeta)

    return tarjetas

def filtrarPorProcesadora(lista, texto):
    tarjetas = []

    for tarjeta in lista:
        if tarjeta.procesadora == texto:
            tarjetas.append(tarjeta)

    return tarjetas

def filtrarPorTipoTarjeta(lista, texto):
    tarjetas = []

    for tarjeta in lista:
        if tarjeta.tipoTarjeta == texto:
            tarjetas.append(tarjeta)

    return tarjetas


def setearTarjeta(texto):
    tarjetasNecesarias = []

    if texto == "Tarjetas Santander de crédito y débito Visa" or texto == "Tarjetas Débito y Crédito Santander Visa" or texto == "Tarjeta Santander de Débito y Crédito Visa" or texto == "Tarjetas Santander Visa Contactless de Débito y Crédito" or texto == "Tarjetas Santander de Crédito y Débito Visa" or texto == "Tarjetas Santander de Débito y Crédito Visa" or texto == "Tarjeta Santander de débito y crédito Visa" or texto == "Tarjeta Santander de Crédito y Débito Visa" or texto == "Tarjetas Santander Visa de crédito y débito" or texto == "Tarjetas Santander Visa Contactless" or texto == "Tarjetas Santander Visa" or texto == "Válido para Tarjetas Santander Visa Crédito y Débito.":
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetas, "Débito"))
        tarjetasNecesarias = tarjetasNecesarias + (filtrarPorProcesadora(tarjetas, "Visa"))
    
    elif texto == "Tarjetas Santander Débito" or texto == "Tarjetas Débito" or texto == "Tarjetas Santander Débito Contactless" or texto == "Tarjetas Santander Visa de Débito" or texto == "Tarjeta de Débito":
        tarjetasNecesarias = filtrarPorTipoTarjeta(tarjetas, "Débito")

    elif texto == "Tarjetas Santander American Express" or texto == "Mastercard y American Express":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "American Express"))

    elif texto == "Tarjetas Santander Recargables":
        tarjetasNecesarias = (filtrarPorSegmento(tarjetas, "Recargable"))

    elif texto == "Tarjetas Santander Mastercard" or texto == "Tarjetas MasterCard"or texto == "Tarjetas Santander MasterCard":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "Mastercard"))

    elif texto == "Tarjeta Women Visa" or texto == "Tarjeta Santander Women Visa":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas,"Visa"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Women"))

    elif texto == "Tarjeta Santander American Express Platinum":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "American Express"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Platinum"))

    elif texto == "Tarjeta Santander American Express Black":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "American Express"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Black"))

    elif texto == "Tarjeta Santander de Crédito Visa" or texto == "Tarjeta Santander de crédito Visa" or texto == "Tarjetas Santander de Crédito Visa" or texto == "Tarjetas Santander de crédito Visa":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "Visa"))
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetasNecesarias, "Crédito"))

    elif texto == "Tarjeta Santander de Crédito":
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetas, "Crédito"))

    elif texto == "Débito y Recargables Visa":
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetas, "Débito"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Recargable"))

    elif texto == "Crédito y Recargables Santander Visa" or texto == "Tarjeta Santander Visa Contactless Recargable":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "Visa"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Recargable"))
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetasNecesarias, "Crédito"))

    elif texto == "Tarjeta Santander Visa Black" or texto == "Débito y Recargables Visa Black":
        tarjetasNecesarias = filtrarPorProcesadora(tarjetas, "Visa")
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Black"))

    elif texto == "Tarjeta Women Mastercard":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "Mastercard"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Women"))

    elif texto == "Tarjeta Women Amex":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "American Express"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Women"))

    elif texto == "Débito y Recargables Visa Platinum":
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetas, "Débito"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Platinum"))

    elif texto == "Tarjeta Santander de Crédito Visa Black":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "Visa"))
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetasNecesarias, "Crédito"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Black"))
    elif texto == "Tarjeta Santander de Crédito Visa Platinum":
        tarjetasNecesarias = (filtrarPorProcesadora(tarjetas, "Visa"))
        tarjetasNecesarias = (filtrarPorTipoTarjeta(tarjetasNecesarias, "Crédito"))
        tarjetasNecesarias = (filtrarPorSegmento(tarjetasNecesarias, "Platinum"))
    else:
        print("CULPABLE: -"+ texto+"-")
        sleep(3000)

    return tarjetasNecesarias


def asignadorCategoria(comercio):

    if comercio in ["Hell's Pizza", "McDonald's", "The Food Market", "Arcor en casa", "Heladerías", "Pantera"]:                
        return "Gastronomía"
    
    elif comercio in ["YPF", "Axion", "Bridgestone"]:                  
        return "vehiculos"
    
    elif comercio in ["Farmacity", "Farmacia Vasallo", "Cortassa", "Óptica Luna", "Universo Garden Angels", "Get the Look", "Peluquerias", "Simplicity", "Rouge", "Ruiz y Roca", "Kiehl's", "24 Glam", "Beauty 24", "Farmafull", "Definit", "Iobella", "Juleriaque", "La Prairie", "Mac", "Mantra", "Parfumerie", "Grimoldi"]:             
        return "farmacia"
    
    elif comercio in ["Rex", "Colorshop", "Imepho", "Universo Garden Angels", "Muebles y sillones", "Pinturerías del Centro", "Pinturerías Pisano"]:             
        return "casa"
    
    elif comercio in ["Aerolíneas Plus", "Latam Pass", "Smiles", "Cerro Chapelco", "La Choza", "Las Balsas", "Tinto"]:   
        return "turismo"
    
    elif comercio in ["Cinépolis"]:         
        return "espectaculos"
    
    elif comercio in ["Open Sports", "Paruolo", "Libus", "Feliza", "Ropa", "Indumentaria", "Sail", "Selú", "Feliziano", "Tiendas Deportivas", "Somos Santas", "Tascani", "Kosiuko", "Devré", "Macowens", "Nike", "Equus", "Cardon Olavarria", "Rochas", "Trip", "Witty Girls", "Broer", "Calzado", "Carteras, mochilas y accesorios", "Etiqueta Negra", "Gola", "Tucci", "Grisino"]:                
        return "moda"
    
    elif comercio in ["Coto", "Disco", "Jumbo", "Vea", "Coto Digital", "Maxiconsumo", "Yaguar"]:   
        return "supermercados"
    
    elif comercio in ["Galaxy Z Fold5 y Z Flip5", "Playstation 5", "Samsung", "Rodó"]:  
        return "electronica"
    
    elif comercio in ["Wall Street English", "CUI"]:                 
        return "educacion"

    elif comercio in ["Creciendo", "Payasin", "Periquita", "Educando", "Kinderland", "Charco", "Magic Toys", "Mi Clavel", "Cachavacha", "Cebra", "City Kids", "Giro Didáctico", "Mono Coco", "Museo de los Niños"]:     
        return "juguetes"
    
    elif comercio in [""]:                  return "regalos"
    elif comercio in [""]:                  return "bebidas"
    elif comercio in ["Swatch", "Carmin"]:                  return "joyeria"
    elif comercio in ["Yenny, El Ateneo, Tematika.com"]:                  return "libreria"
    elif comercio in [""]:                  return "mascotas"
    elif comercio in ["Pedidos Ya Market", "PedidosYa", "PedidosYa Plus", "ShopGallery", "Style Store"]:                  return "servicios"
    else: return "varios"



