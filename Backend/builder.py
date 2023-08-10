import firebase_admin
from firebase_admin import credentials, db

cred = credentials.Certificate("../certificados/offerhub-proyectofinal-firebase-adminsdk-szyk2-b5857b6480.json")
firebase_admin.initialize_app(cred, {'databaseURL': 'https://offerhub-proyectofinal-default-rtdb.firebaseio.com/'})

def escribirDB(clase, datos):
    ref = db.reference("/" + clase)
    ids_guardados = []

    for dato in datos:
        new_ref = ref.push(dato)
        ids_guardados.append(new_ref.key)

    return ids_guardados

def obtenerIdPorContenido(coleccion, campo, valor):
    ref = db.reference(coleccion)
    snapshot = ref.get()

    if snapshot:
        valor_minuscula = valor.lower()
        for key, data in snapshot.items():
            if campo in data and (data[campo].lower() == valor_minuscula or valor_minuscula in data[campo].lower()):
                return key

    return None  # Si no se encontró el documento
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
