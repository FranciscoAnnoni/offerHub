from enum import Enum
class TipoDePromocion(Enum):
    REINTEGRO = "reintegro"
    DESCUENTO = "descuento"
    CUOTAS = "cuotas"

class TipoDeCategoria(Enum):
    GASTRONOMIA = "gastronomia"
    VEHICULOS = "vehiculos"
    SALUDYBIENESTAR = "salud y bienestar"
    HOGAR = "hogar"
    VIAJESYTURISMO = "viajes y turismo"
    ENTRETENIMIENTO = "entretenimiento"
    INDUMENTARIA = "indumentaria"
    SUPERMERCADOS = "supermercados"
    ELECTRONICA = "electronica"
    EDUCACION = "educacion"
    NIÑOS = "niños" 
    JUGUETES = "juguetes" #Podria ir con niños tranquilamente, por las dudas lo dejo asi
    REGALOS = "regalos"
    BEBIDAS = "bebidas"
    JOYERIA = "joyeria"
    LIBRERIA = "libreria"
    MASCOTAS = "mascotas"
    SERVICIOS = "servicios"