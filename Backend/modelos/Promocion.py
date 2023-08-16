import builder as builder
from CategoriaPromocion import CategoriaPromocion
from TipoPromocion import TipoPromocion
from dateutil.parser import parse

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

    def setearTipoPromocion(self,tituloPromo,tope):
        self.tipoPromocion=TipoPromocion.obtenerTipoPromocion(tituloPromo,tope)

    def guardar(self):
            idPromocion=builder.obtenerIdPorContenido("Promocion",{"titulo":self.titulo,"url":self.url,"vigenciaDesde":self.vigenciaDesde,"vigenciaHasta":self.vigenciaHasta})
            if idPromocion==None:
                return builder.escribirDB("Promocion",[vars(self)])[0]
            else:
                return idPromocion
    