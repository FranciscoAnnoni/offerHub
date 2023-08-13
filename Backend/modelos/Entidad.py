import builder as builder

class Entidad:
    # nombre
    # tipo

    def guardar(self):
        idEntidad=builder.obtenerIdPorContenido("Entidad",{"nombre":self.nombre})
        if idEntidad==None:
            return builder.escribirDB("Entidad",[vars(self)])[0]
        else:
            return idEntidad

    def obtenerIdEntidadPorNombre(self):
        return builder.obtenerIdPorContenido("Entidad",{"nombre":self.nombre})
    