import firebase_admin
from firebase_admin import credentials, db

cred = credentials.Certificate("../certificados/offerhub-proyectofinal-firebase-adminsdk-szyk2-b5857b6480.json")
firebase_admin.initialize_app(cred, {'databaseURL': 'https://offerhub-proyectofinal-default-rtdb.firebaseio.com/'})

def escribirDB(clase, datos):
    ref = db.reference("/" + clase)
    for dato in datos:
        ref.push(dato)

# Crear instancias de Promocion
"""
promo1 = Promocion("Sofi", "Galicia")
promo2 = Promocion("Axel", "Santander")
promociones = [promo1, promo2]

# Escribir en la base de datos
escribirDB("Promocion", promociones)
"""

# Realizar una consulta filtrando por el campo "banco"
ref = db.reference("/Promocion")
resultados = ref.order_by_child("nombre").equal_to("Juan").get()

for key, value in resultados.items():
    print(key, "=>", value)
