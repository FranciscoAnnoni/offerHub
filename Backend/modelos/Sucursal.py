import builder as builder

class Sucursal:
    # direccion
    # latitud (coordenadas)
    # longitud (coordenadas)
    # idComercio

    def guardar(self):
        idSucursal=builder.obtenerIdPorContenido("Sucursal",{"latitud":self.latitud,"longitud":self.longitud, "idComercio":self.idComercio,"direccion":self.direccion})
        if idSucursal==None:
            return builder.escribirDB("Sucursal",[vars(self)])[0]
        else:
            return idSucursal