import builder as builder

class Comercio:
    # nombre
    # url
    # categoria
    def guardar(self):
        idComercio=builder.obtenerIdPorContenido("Comercio",{"nombre":self.nombre,"categoria":self.categoria})
        if idComercio==None:
            return builder.escribirDB("Comercio",[vars(self)])[0]
        else:
            return idComercio

    def existeComercio(self):
        return self.obtenerIdComercioPorNombre() != None

    def obtenerIdComercioPorNombre(self):
        return builder.obtenerIdPorContenido("Comercio",{"nombre":self.nombre})
    