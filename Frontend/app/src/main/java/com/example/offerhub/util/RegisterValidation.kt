package com.example.offerhub.util

sealed class RegisterValidation(){
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}

data class RegisterFieldsState(
    val email: RegisterValidation,
    val password: RegisterValidation
)

data class RegisterFieldsStateUser(
    val email: RegisterValidation,
    val password: RegisterValidation,
    val password2: RegisterValidation
)

data class RegisterFieldsStatePartner(
    val email: RegisterValidation,
    val password: RegisterValidation,
    val password2: RegisterValidation
)

data class RegisterFieldsStatePartnerCuil(
    val cuil: RegisterValidation,
    val categoria: RegisterValidation
)
