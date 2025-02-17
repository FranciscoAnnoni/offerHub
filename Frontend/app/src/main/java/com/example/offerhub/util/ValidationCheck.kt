package com.example.offerhub.util

import android.util.Log
import android.util.Patterns
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

fun validateEmail(email:String?): RegisterValidation{
    if (email != null) {
        if (email.isEmpty())
            return RegisterValidation.Failed("El campo Email no puede estar vacio")
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Formato de Email incorrecto")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation{
    if (password.isEmpty())
        return RegisterValidation.Failed("El campo Contraseña no puede estar vacio")
    if (password.length < 6)
        return RegisterValidation.Failed("La Contraseña debe tener 6 o mas caracteres")
    return RegisterValidation.Success
}

fun validatePasswords(password1: String,password2: String): RegisterValidation{
    if (password1 != password2)
        return RegisterValidation.Failed("La Contraseñas deben coincidir")

    return RegisterValidation.Success
}


fun validateCuil(cuil: String): RegisterValidation {
    if (cuil.isEmpty())
        return RegisterValidation.Failed("El campo CUIL no puede estar vacio")
    if (cuil.length == 11 && cuil.all { it.isDigit() }) {
        return RegisterValidation.Success
    } else {
        return RegisterValidation.Failed("Su CUIL debe tener exactamente 11 dígitos.")
    }
}

fun validateCategoria (categoria: String): RegisterValidation {
    if (categoria == "Seleccione una Opcion")
        return RegisterValidation.Failed("Porfavor seleccione una opcion valida")

    return RegisterValidation.Success
}