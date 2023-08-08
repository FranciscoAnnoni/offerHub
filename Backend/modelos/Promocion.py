import builder as builder
from dateutil.parser import parse

class Promocion:
    # titulo
    # proveedor
    # url
    # tope
    # vigenciaDesde
    # vigenciaHasta
    # dias
    # tyc
    # descripcion
    def setearFecha(self,atributo,valor):
        fecha_objeto = parse(valor)
        setattr(self, atributo,fecha_objeto.strftime("%Y-%m-%d"))

    def guardar(self):
        builder.escribirDB("Promocion",[vars(self)])
    