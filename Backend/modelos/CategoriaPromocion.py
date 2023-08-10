from enum import Enum

class CategoriaPromocion:
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
    REGALOS = "regalos"
    BEBIDAS = "bebidas"
    JOYERIA = "joyeria"
    LIBRERIA = "libreria"
    MASCOTAS = "mascotas"
    SERVICIOS = "servicios"
    OTROS = "otros"

    SINONIMOS = {
        GASTRONOMIA: [GASTRONOMIA, "resto", "heladerias", "desayuno"],
        VEHICULOS: [VEHICULOS, "auto", "combustible", "automotor", "automotores", "bicicleterias", "automovil"],
        SALUDYBIENESTAR: [SALUDYBIENESTAR, "belleza", "cuidado personal", "bienestar", "farmacias", "farmacia", "perfumerias"],
        HOGAR: [HOGAR, "casa", "hogar y decoracion", "cuidado personal", "deco y hogar"],
        VIAJESYTURISMO: [VIAJESYTURISMO, "turismo", "viajes"],
        ENTRETENIMIENTO: [ENTRETENIMIENTO, "espectaculos"],
        INDUMENTARIA: [INDUMENTARIA, "moda", "moda y accesorios"],
        SUPERMERCADOS: [SUPERMERCADOS, "super"],
        ELECTRONICA: [ELECTRONICA, "tecnologia", "electro and tecnologia", "electro y gaming"],
        EDUCACION: [EDUCACION, "capacitacion"],
        NIÑOS: [NIÑOS, "ninos", "juguetes", "jugueterias"],
        REGALOS: [REGALOS],
        BEBIDAS: [BEBIDAS, "vinos bodega", "vinoteca"],
        JOYERIA: [JOYERIA],
        LIBRERIA: [LIBRERIA, "librerias"],
        MASCOTAS: [MASCOTAS],
        SERVICIOS: [SERVICIOS],
        OTROS: [OTROS, "varios", "otras compras", "servicios", "otros beneficios"]
    }
