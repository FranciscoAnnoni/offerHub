package com.example.offerhub.contexts

import android.content.Context

object ApplicationContextProvider {
    private var context: Context? = null

    fun initialize(applicationContext: Context) {
        if (context == null) {
            context = applicationContext
        }
    }

    fun getApplicationContext(): Context {
        if (context == null) {
            throw IllegalStateException("ApplicationContextProvider no ha sido inicializado.")
        }
        return context!!
    }
}
