class TipoPromocion:
    REINTEGRO = "Reintegro"
    DESCUENTO = "Descuento"
    CUOTAS = "Cuotas"
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
        else:
            tipo_promocion = TipoPromocion.OTRO

        return tipo_promocion
