package com.example.offerhub.data

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.offerhub.R

class Categoria {
    var nombre: String
    var logo: Drawable

    constructor(context: Context, nombre: String, filename: String) {
        this.nombre = nombre
        val resourceId = context.resources.getIdentifier(filename, "drawable", context.packageName)
        if (resourceId != 0) {
            this.logo = ContextCompat.getDrawable(context, resourceId) ?: ContextCompat.getDrawable(context, R.drawable.cat_default)!!
        } else {
            this.logo = ContextCompat.getDrawable(context, R.drawable.cat_default)!!
        }
    }
}