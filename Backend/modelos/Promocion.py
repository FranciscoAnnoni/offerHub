from enum import Enum

class TipoDePromocion(Enum):
    REINTEGRO = "reintegro"
    DESCUENTO = "descuento"
    CUOTAS = "cuotas"

class Promocion:
    def __init__(self, nombre, descripcion, tipoDePromocion, porcentaje, topeDeReintegro, cantCuotas, sinInteres, tyc, listadoSucursales):
        self.__nombre = nombre
        self.__descripcion = descripcion
        self.__tipoDePromocion = tipoDePromocion
        self.__porcentaje = porcentaje
        self.__topeDeReintegro = topeDeReintegro
        self.__cantCuotas = cantCuotas
        self.__sinInteres = sinInteres
        self.__tyc = tyc
        self.__listadoSucursales = listadoSucursales
    
    # Getters
    def get_nombre(self):
        return self.__nombre
    
    def get_descripcion(self):
        return self.__descripcion
    
    def get_tipoDePromocion(self):
        return self.__tipoDePromocion
    
    def get_porcentaje(self):
        return self.__porcentaje
    
    def get_topeDeReintegro(self):
        return self.__topeDeReintegro
    
    def get_cantCuotas(self):
        return self.__cantCuotas
    
    def get_sinInteres(self):
        return self.__sinInteres
    
    def get_tyc(self):
        return self.__tyc
    
    def get_listadoSucursales(self):
        return self.__listadoSucursales
    
    # Setters
    def set_nombre(self, nombre):
        self.__nombre = nombre
    
    def set_descripcion(self, descripcion):
        self.__descripcion = descripcion
    
    def set_tipoDePromocion(self, tipoDePromocion):
        self.__tipoDePromocion = tipoDePromocion
    
    def set_porcentaje(self, porcentaje):
        self.__porcentaje = porcentaje
    
    def set_topeDeReintegro(self, topeDeReintegro):
        self.__topeDeReintegro = topeDeReintegro
    
    def set_cantCuotas(self, cantCuotas):
        self.__cantCuotas = cantCuotas
    
    def set_sinInteres(self, sinInteres):
        self.__sinInteres = sinInteres
    
    def set_tyc(self, tyc):
        self.__tyc = tyc
    
    def set_listadoSucursales(self, listadoSucursales):
        self.__listadoSucursales = listadoSucursales