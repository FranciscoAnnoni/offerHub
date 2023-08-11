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
        GASTRONOMIA: ["resto", "heladerias", "desayuno", "gastronomia","comidas rapidas","restaurantes y bares","cafeteria / dulceria","delivery","tiendas gourmet"],
        VEHICULOS: ["vehiculos", "auto", "combustible", "automotor", "automotores", "bicicleterias", "automovil", "lavadero", "neumaticos"],
        SALUDYBIENESTAR: ["belleza", "cuidado personal", "bienestar", "farmacias", "farmacia", "perfumerias", "salud y bienestar", "farmacias y perfumerias", "centros esteticos", "salud", "gimnasios", "peluquerias"],
        HOGAR: [HOGAR, "casa", "hogar y decoracion", "cuidado personal", "deco y hogar", "muebles", "pinturerias", "baños y cocina", "jardines y exteriores","huertas y jardines","tiendas","seguridad"],
        VIAJESYTURISMO: ["viajes y turismo", "turismo", "viajes","hoteles","actividades turisticas","agencia turistica"],
        ENTRETENIMIENTO: ["entretenimiento", "espectaculos","teatros","cines","teatro","cine","aventuras","recitales y conciertos"],
        INDUMENTARIA: ["indumentaria", "moda", "moda y accesorios","calzados","accesorios","infantiles"],
        SUPERMERCADOS: ["supermercados", "super"],
        ELECTRONICA: ["electronica", "tecnologia", "electro and tecnologia", "electro y gaming"],
        EDUCACION: ["educacion", "capacitacion"],
        NIÑOS: ["niños", "ninos", "juguetes", "jugueterias"],
        REGALOS: ["regalos"],
        BEBIDAS: ["bebidas", "vinos bodega", "vinoteca","vinotecas"],
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

