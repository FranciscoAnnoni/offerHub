import builder as builder

class Tarjeta:
    # entidad
    # procesadora
    # segmento 
    # tipoTarjeta

    def guardar(self):
        idTarjeta=builder.obtenerIdPorContenido("Tarjeta",{"entidad":self.entidad,"procesadora":self.procesadora, "segmento":self.segmento, "tipoTarjeta":self.tipoTarjeta})
        if idTarjeta==None:
            return builder.escribirDB("Tarjeta",[vars(self)])[0]
        else:
            return idTarjeta

    def existeTarjeta(self):
        return self.obtenerIdTarjetaPorNombre() != None

    def obtenerIdTarjetaPorNombre(self):
        return builder.obtenerIdPorContenido("Tarjeta",{"nombre":self.nombre})
    