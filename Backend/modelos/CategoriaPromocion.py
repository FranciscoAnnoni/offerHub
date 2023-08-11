from enum import Enum
#hacer pip install unidecode
from unidecode import unidecode


class CategoriaPromocion:
    GASTRONOMIA = "Gastronomía"
    VEHICULOS = "Vehículos"
    SALUDYBIENESTAR = "Salud y Bienestar"
    HOGAR = "Hogar"
    VIAJESYTURISMO = "Viajes y Turismo"
    ENTRETENIMIENTO = "Entretenimiento"
    INDUMENTARIA = "Indumentaria"
    SUPERMERCADOS = "Supermercados"
    ELECTRONICA = "Electrónica"
    EDUCACION = "Educación"
    NIÑOS = "Niños" 
    REGALOS = "Regalos"
    BEBIDAS = "Bebidas"
    JOYERIA = "Joyería"
    LIBRERIA = "Librerías"
    MASCOTAS = "Mascotas"
    SERVICIOS = "Servicios"
    OTROS = "Otros"

    SINONIMOS = {
        GASTRONOMIA: ["resto", "heladerias", "desayuno", "gastronomia"],
        VEHICULOS: ["vehiculos", "auto", "combustible", "automotor", "automotores", "bicicleterias", "automovil"],
        SALUDYBIENESTAR: ["belleza", "cuidado personal", "bienestar", "farmacias", "farmacia", "perfumerias", "salud y bienestar"],
        HOGAR: [HOGAR, "casa", "hogar y decoracion", "cuidado personal", "deco y hogar"],
        VIAJESYTURISMO: ["viajes y turismo", "turismo", "viajes"],
        ENTRETENIMIENTO: ["entretenimiento", "espectaculos"],
        INDUMENTARIA: ["indumentaria", "moda", "moda y accesorios"],
        SUPERMERCADOS: ["supermercados", "super"],
        ELECTRONICA: ["electronica", "tecnologia", "electro and tecnologia", "electro y gaming"],
        EDUCACION: ["educacion", "capacitacion"],
        NIÑOS: ["niños", "ninos", "juguetes", "jugueterias"],
        REGALOS: ["regalos"],
        BEBIDAS: ["bebidas", "vinos bodega", "vinoteca"],
        JOYERIA: ["joyeria"],
        LIBRERIA: ["libreria", "librerias"],
        MASCOTAS: ["mascotas"],
        SERVICIOS: ["servicios"],
        OTROS: [OTROS, "varios", "otras compras", "servicios", "otros beneficios"]
    }

    @staticmethod
    def obtenerCategoria(categoria):
        nombreCategoria = unidecode(categoria.lower()) #Le saca las mayúsculas y las tíldes
        categoria_encontrada = False
        
        for clave, valores in CategoriaPromocion.SINONIMOS.items():
            if nombreCategoria in valores:              
                categoria_encontrada = True
                return clave

        if not categoria_encontrada:
            return CategoriaPromocion.OTROS

