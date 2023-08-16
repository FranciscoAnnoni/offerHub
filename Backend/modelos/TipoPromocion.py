class TipoPromocion:
    REINTEGRO = "Reintegro"
    DESCUENTO = "Descuento"
    CUOTAS = "Cuotas"
    DOS_X_UNO = "2x1"
    OTRO = "Otro"

    @staticmethod
    def obtenerTipoPromocion(tituloPromo, tope):
        tituloPromo=tituloPromo.lower()
        tope=tope.strip()
        if len(tope) > 0:
            tipo_promocion = TipoPromocion.REINTEGRO
        elif "%" in tituloPromo:
            tipo_promocion = TipoPromocion.DESCUENTO
        elif "cuota" in tituloPromo:
            tipo_promocion = TipoPromocion.CUOTAS
        elif "2x1" in tituloPromo.replace(" ","") or "2X1" in tituloPromo.replace(" ",""):
            tipo_promocion = TipoPromocion.DOS_X_UNO
        else:
            tipo_promocion = TipoPromocion.OTRO

        return tipo_promocion
