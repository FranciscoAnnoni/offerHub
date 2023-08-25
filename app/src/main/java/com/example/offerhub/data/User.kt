package com.example.offerhub.data



class User (
    val nameAndLastName: String,
    val email: String,
    var imagePath: String = ""
){
    constructor(): this("","","")


}