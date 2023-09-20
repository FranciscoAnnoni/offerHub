package com.example.offerhub.viewmodel

import UserViewModel

object UserViewModelSingleton {
    private var userViewModel: UserViewModel? = null

    fun initialize(userViewModel: UserViewModel) {
        this.userViewModel = userViewModel
    }

    fun getUserViewModel(): UserViewModel {
        if(userViewModel==null){
            initialize(UserViewModel())
        }
        return requireNotNull(userViewModel) { "UserViewModel no inicializado" }
    }
}
