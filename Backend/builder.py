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

def obtenerIdPorContenido(coleccion, condiciones):
    ref = db.reference(coleccion)
    snapshot = ref.get()

    if snapshot:
        for key, data in snapshot.items():
            cumple_condiciones = all(
                campo in data and (data[campo].lower() == valor.lower() or valor.lower() in data[campo].lower())
                for campo, valor in condiciones.items()
            )
            if cumple_condiciones:
                return key

    return None  

def eliminar(coleccion, campo=None, valor=None):
    ref = db.reference(coleccion)
    snapshot = ref.get()

    if snapshot:
        for key, data in snapshot.items():
            if campo is None or (campo == "key" and key == valor) or (campo in data and data[campo] == valor):
                ref.child(key).delete()

def eliminarPorId(coleccion,id):
	self.eliminar(coleccion, "key", id)