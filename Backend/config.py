import builder as builder
# Si testing esta en True, las promociones y comercios se van a borrar cada vez que corramos un proceso.
TESTING = True
#CERT_PATH="../certificados/offerhub-proyectofinal-firebase-adminsdk-szyk2-b5857b6480.json"
#DB_URL='https://offerhub-proyectofinal-default-rtdb.firebaseio.com/'
#CERT_PATH="../certificados/oh-backend-848a1-firebase-adminsdk-20hoh-1c1b598f66.json"
#DB_URL='https://oh-backend-848a1-default-rtdb.firebaseio.com/'
#CERT_PATH="../certificados/oh-bkd2-firebase-adminsdk-89e1s-0eaaed445f.json"
#DB_URL='https://oh-bkd2-default-rtdb.firebaseio.com/'
#CERT_PATH="../certificados/oh-backend3-firebase-adminsdk-q1k3y-1ce0f31fb1.json"
#DB_URL="https://oh-backend3-default-rtdb.firebaseio.com/"
#CERT_PATH="../certificados/oh--bknd-4-firebase-adminsdk-n47zm-b35197edf7.json"
#DB_URL="https://oh--bknd-4-default-rtdb.firebaseio.com/"
#CERT_PATH="../certificados/oh-backend-5-firebase-adminsdk-zdmlf-01fcca0491.json"
#DB_URL="https://oh-backend-5-default-rtdb.firebaseio.com/"
#DB_URL="https://pistacho-21adc-default-rtdb.firebaseio.com/"
#CERT_PATH="../certificados/pistacho-21adc-firebase-adminsdk-6tzpf-766d5badf1.json"
#DB_URL="https://prueba-final-fc417-default-rtdb.firebaseio.com/"
#CERT_PATH="../certificados/prueba-final-fc417-firebase-adminsdk-dxhtg-660fb7346b.json"
#DB_URL="https://ahora-si-8dec8-default-rtdb.firebaseio.com/"
#CERT_PATH="../certificados/ahora-si-8dec8-firebase-adminsdk-4ttvo-c94f49b38c.json"
#DB_URL="https://carlitos-65fd9-default-rtdb.firebaseio.com/"
#CERT_PATH="../certificados/carlitos-65fd9-firebase-adminsdk-4ba5r-ce04cc7d6d.json"
DB_URL="https://cristian-d735c-default-rtdb.firebaseio.com/"
CERT_PATH="../certificados/cristian-d735c-firebase-adminsdk-2fkvb-874b436a5f.json"

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
        