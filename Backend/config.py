import builder as builder
# Si testing esta en True, las promociones y comercios se van a borrar cada vez que corramos un proceso.
TESTING = False
#CERT_PATH="../certificados/offerhub-proyectofinal-firebase-adminsdk-szyk2-b5857b6480.json"
#DB_URL='https://offerhub-proyectofinal-default-rtdb.firebaseio.com/'
#CERT_PATH="../certificados/oh-backend-848a1-firebase-adminsdk-20hoh-1c1b598f66.json"
#DB_URL='https://oh-backend-848a1-default-rtdb.firebaseio.com/'
CERT_PATH="../certificados/oh-bkd2-firebase-adminsdk-89e1s-0eaaed445f"
DB_URL='https://oh-bkd2-default-rtdb.firebaseio.com/'

def setearEntorno():
    eliminarDatosPrevios()
    # Aca se agregarian todas las funciones que queramos correr para setear el entorno antes de correr cada scraping

def eliminarDatosPrevios():
    if TESTING:
        builder.eliminar("Promocion")
        builder.eliminar("Comercio")
        builder.eliminar("Tarjeta")
        builder.eliminar("Entidad")
        builder.eliminar("Sucursal")
        