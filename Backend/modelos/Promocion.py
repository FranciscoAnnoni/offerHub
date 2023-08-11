import builder as builder
from CategoriaPromocion import CategoriaPromocion
from dateutil.parser import parse

class Promocion:
    # titulo
    # proveedor
    # url
    # tope
    # vigenciaDesde
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

    def guardar(self):
        builder.escribirDB("Promocion",[vars(self)])
    