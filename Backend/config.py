import builder as builder
# Si testing esta en True, las promociones y comercios se van a borrar cada vez que corramos un proceso.
TESTING = True

def setearEntorno():
    eliminarDatosPrevios()
    # Aca se agregarian todas las funciones que queramos correr para setear el entorno antes de correr cada scraping

def eliminarDatosPrevios():
    if TESTING:
        builder.eliminar("Promocion")
        builder.eliminar("Comercio")
        builder.eliminar("Tarjeta")
        builder.eliminar("Entidad")