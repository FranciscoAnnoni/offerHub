package com.example.offerhub

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Globals {
    var usuario:Usuario?=null

    suspend fun asegurarUsuario(): Usuario? {
        return withContext(Dispatchers.IO) {
            if (usuario == null) {
                usuario = Funciones().traerUsuarioActual()
            }
            usuario
        }
    }
}