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
    # tipoPromocion
    
    def setearFecha(self,atributo,valor):
        fecha_objeto = parse(valor)
        lista = fecha_objeto.strftime("%Y-%m-%d").split("-")
        # DEJO ESTO ROTO COMO RECORDATORIO. LAS FECHAS CON DIA MENOR A 12 LAS ESTA PONIENDO AL REVÉS. CHEQUEAR SI CON ESTO SE ARREGLA
        print(lista[1]+" "+ lista[2])
        if int(lista[1]) < int(lista[2]) and 13 > int(lista[2]):
            fechaFinal = lista[0]+"-"+lista[2]+"-"+lista[1] 
            setattr(self, atributo,fechaFinal)
            print(fechaFinal)
        else:
            fechaFinal = fecha_objeto.strftime("%Y-%m-%d").split(" ")[0]
            setattr(self, atributo,fechaFinal)
            print(fechaFinal)

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
