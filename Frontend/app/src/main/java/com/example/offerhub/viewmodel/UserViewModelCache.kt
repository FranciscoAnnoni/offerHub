package com.example.offerhub.viewmodel
import UserViewModel
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.offerhub.Promocion
import com.example.offerhub.Usuario
import com.example.offerhub.contexts.ApplicationContextProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserViewModelCache() {

    private val sharedPreferences: SharedPreferences =
        ApplicationContextProvider.getApplicationContext().getSharedPreferences("user_view_model_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun guardarUserViewModel(userViewModel: UserViewModel) {
        val currentUser=FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
            val userViewModelJson = gson.toJson(userViewModel)
            vaciarCache()
            sharedPreferences.edit().putString(currentUser.uid, userViewModelJson).apply()
        }
    }

    fun vaciarCache() {
        val currentUser=FirebaseAuth.getInstance().currentUser
        if(currentUser!=null) {
            sharedPreferences.edit().remove(currentUser.uid).apply()
            // También puedes borrar otros elementos del caché, si los tienes, de manera similar
        }
    }
    fun cargarUserViewModel(): UserViewModel? {
        val currentUser=FirebaseAuth.getInstance().currentUser
        Log.d("Cacheado",sharedPreferences.all.keys.joinToString(","))
        if(currentUser!=null) {
            Log.d("Cargar User Cacheado","1")
            val userViewModelJson = sharedPreferences.getString(currentUser.uid, null)
            val type = object : TypeToken<UserViewModel>() {}.type
            Log.d("Cargar User Cacheado","2")
            return if (userViewModelJson != null) {
                gson.fromJson(userViewModelJson, type)
            } else {
                null
            }
        }
        Log.d("Cargar User Cacheado","-1")
        return null
    }



    // Implementa métodos similares para favoritos y reintegros
}
