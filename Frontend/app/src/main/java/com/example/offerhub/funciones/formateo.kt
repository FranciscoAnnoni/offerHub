package com.example.offerhub.funciones

import java.text.Normalizer
import java.util.regex.Pattern
import android.graphics.Bitmap
import android.graphics.Color
import com.example.offerhub.R
import org.threeten.bp.LocalDate

import java.text.SimpleDateFormat
import java.util.Locale

fun removeAccents(input: String?): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(normalized).replaceAll("")
}

fun obtenerColorMayoritario(bitmap: Bitmap): Int {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    // Mapa para contar la ocurrencia de cada color
    val colorCountMap = HashMap<Int, Int>()

    // Recorre los píxeles de la imagen y cuenta la ocurrencia de cada color
    for (color in pixels) {
        if (colorCountMap.containsKey(color)) {
            colorCountMap[color] = colorCountMap[color]!! + 1
        } else {
            colorCountMap[color] = 1
        }
    }

    // Encuentra el color con mayor ocurrencia
    var colorMayoritario = Color.BLACK // Color predeterminado en caso de empate o imagen vacía
    var maxOcurrencia = 0

    for ((color, ocurrencia) in colorCountMap) {
        if (ocurrencia > maxOcurrencia) {
            colorMayoritario = color
            maxOcurrencia = ocurrencia
        }
    }

    return colorMayoritario
}

fun getContrastColor(backgroundColor: Int, threshold: Int = 128): Int {
    val red = (backgroundColor shr 16) and 0xFF
    val green = (backgroundColor shr 8) and 0xFF
    val blue = backgroundColor and 0xFF
    val brightness = (red * 299 + green * 587 + blue * 114) / 1000
    return if (brightness > threshold) {
        Color.BLACK // Fondo claro, usar texto negro
    } else {
        Color.WHITE // Fondo oscuro, usar texto blanco
    }
}

fun getFavResource(isFavorite: Boolean): Int {
    if (isFavorite) {
        return R.drawable.ic_fav_selected
    } else {
        return R.drawable.ic_fav
    }
}


fun formatearFecha(fecha: LocalDate?): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = inputFormat.parse(fecha.toString())
    return outputFormat.format(date)
}

