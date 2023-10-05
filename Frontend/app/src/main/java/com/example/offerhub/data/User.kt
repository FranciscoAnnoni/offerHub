package com.example.offerhub.data


class User (
    val nameAndLastName: String,
    val email: String,
    var id: String = "",
    var tarjetas: List<String?>? = null,
    var favoritos: List<String?>? = null,
    var wishlistComercio: List<String?>? = null,
    var wishlistRubro: List<String?>? = null,
    var promocionesReintegro: List<String?>? = null

){
    constructor(): this("","","",null,null,null,null,null)
}

class UserPartner (
    val nombreDeEmpresa: String,
    val cuil: String,
    val email: String,
    var id: String = "",
    var listaPromociones: List<String?>? = null

){
    constructor() : this("","","","",null)
}