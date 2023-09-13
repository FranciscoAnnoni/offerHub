package com.example.offerhub.util

import android.util.Patterns

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