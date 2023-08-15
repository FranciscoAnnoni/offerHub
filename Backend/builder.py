import firebase_admin
from firebase_admin import credentials, db
import config as config

cred = credentials.Certificate(config.CERT_PATH)
firebase_admin.initialize_app(cred, {'databaseURL': config.DB_URL})

def escribirDB(clase, datos, id=None):
    ref = db.reference("/" + clase)
    ids_guardados = []

    if id is not None:
        ref.child(id).update(datos)
        ids_guardados.append(id)
    else:
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
                campo in data and (str(data[campo]).lower() == str(valor.lower()) or str(valor.lower()) in str(data[campo]).lower())
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