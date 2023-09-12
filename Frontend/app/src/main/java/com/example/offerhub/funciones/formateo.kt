package com.example.offerhub.funciones

import java.text.Normalizer
import java.util.regex.Pattern

fun removeAccents(input: String?): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(normalized).replaceAll("")
}
