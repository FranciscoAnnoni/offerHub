import builder as builder
from CategoriaPromocion import CategoriaPromocion
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

    def setearCategoria(self, nombreCategoria):
        categoria_encontrada = False
        for clave, valores in CategoriaPromocion.SINONIMOS.items():
            if nombreCategoria.lower() in valores:
                self.categoria = clave
                categoria_encontrada = True
                break

        if not categoria_encontrada:
            self.categoria = CategoriaPromocion.OTROS

    def guardar(self):
        builder.escribirDB("Promocion",[vars(self)])
    