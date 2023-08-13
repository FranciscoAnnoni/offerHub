import builder as builder

class Sucursal:
    # direccion
    # coordenadas
    # idComercio

    def guardar(self):
        idSucursal=builder.obtenerIdPorContenido("Sucursal",{"coordenadas":self.coordenadas, "idComercio":self.idComercio})
        if idSucursal==None:
            return builder.escribirDB("Sucursal",[vars(self)])[0]
        else:
            return idSucursal
    