import builder as builder
from CategoriaPromocion import CategoriaPromocion
from TipoPromocion import TipoPromocion
from dateutil.parser import parse
import re

class Promocion:
    # titulo
    # proveedor
    # url
    # tope
    # vigenciaDesde
    # condiciones
    # tarjetas
    # vigenciaHasta
    # dias
    # tyc
    # descripcion
    def setearFecha(self,atributo,valor):
        fecha_objeto = parse(valor)
        setattr(self, atributo,fecha_objeto.strftime("%Y-%m-%d"))

    def setearCategoria(self, nombreCategoria):
        self.categoria=CategoriaPromocion.obtenerCategoria(nombreCategoria)

    def setearTipoPromocion(self,tituloPromo,tope=""):
        self.tipoPromocion=TipoPromocion.obtenerTipoPromocion(tituloPromo,tope)

    def guardar(self):
            idPromocion=builder.obtenerIdPorContenido("Promocion",{"titulo":self.titulo,"url":self.url,"vigenciaDesde":self.vigenciaDesde,"vigenciaHasta":self.vigenciaHasta})
            if idPromocion==None:
                return builder.escribirDB("Promocion",[vars(self)])[0]
            else:
                return idPromocion
    
    def obtenerPorcentajeYCantCuotas(self,titulo):
        promo_info = {"porcentaje": None, "cuotas": None}
        titulo=titulo.lower()
        # Buscar porcentaje utilizando expresión regular
        percent_match = re.search(r'(\d+)\s*%', titulo)
        if percent_match:
            promo_info["porcentaje"] = percent_match.group(1)
        
        # Buscar cuotas utilizando expresión regular
        cuotas_match = re.search(r'(\d+)\s*cuotas', titulo)
        if cuotas_match:
            promo_info["cuotas"] = cuotas_match.group(1)
        
        return promo_info