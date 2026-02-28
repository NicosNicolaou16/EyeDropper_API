package com.nicos.eye_dropper_api.utils

import kotlin.math.pow
import kotlin.math.sqrt

fun Int.toHexCode(): String = String.format("#%08X", this)

fun hexToRgb(hex: String): Triple<Int, Int, Int> {
    val clean = hex.trimStart('#')
    val value = clean.toLong(16).toInt()
    return Triple(
        (value shr 16) and 0xFF,
        (value shr 8) and 0xFF,
        value and 0xFF
    )
}

fun rgbToLab(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
    // Step 1: RGB -> Linear RGB
    fun toLinear(c: Double): Double {
        val v = c / 255.0
        return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }

    val lr = toLinear(r.toDouble())
    val lg = toLinear(g.toDouble())
    val lb = toLinear(b.toDouble())

    // Step 2: Linear RGB -> XYZ (D65 illuminant)
    val x = lr * 0.4124564 + lg * 0.3575761 + lb * 0.1804375
    val y = lr * 0.2126729 + lg * 0.7151522 + lb * 0.0721750
    val z = lr * 0.0193339 + lg * 0.1191920 + lb * 0.9503041

    // Step 3: XYZ -> LAB
    fun f(t: Double): Double {
        return if (t > 0.008856) t.pow(1.0 / 3.0) else (7.787 * t) + (16.0 / 116.0)
    }

    val fx = f(x / 0.95047)
    val fy = f(y / 1.00000)
    val fz = f(z / 1.08883)

    val L = (116.0 * fy) - 16.0
    val A = 500.0 * (fx - fy)
    val B = 200.0 * (fy - fz)

    return Triple(L, A, B)
}

data class NamedColor(val name: String, val hex: String)

fun getClosestColorName(hex: String): String {
    val namedColors = listOf(
        NamedColor("Red",     "#FF0000"),
        NamedColor("Green",   "#008000"),
        NamedColor("Blue",    "#0000FF"),
        NamedColor("Yellow",  "#FFFF00"),
        NamedColor("Orange",  "#FFA500"),
        NamedColor("Purple",  "#800080"),
        NamedColor("Pink",    "#FFC0CB"),
        NamedColor("Black",   "#000000"),
        NamedColor("White",   "#FFFFFF"),
        NamedColor("Gray",    "#808080"),
        NamedColor("Brown",   "#A52A2A"),
        NamedColor("Cyan",    "#00FFFF"),
        NamedColor("Magenta", "#FF00FF"),
        NamedColor("Lime",    "#00FF00"),
        NamedColor("Indigo",  "#4B0082"),
        NamedColor("Violet",  "#EE82EE"),
        NamedColor("Teal",    "#008080"),
        NamedColor("Navy",    "#000080"),
        NamedColor("Maroon",  "#800000"),
        NamedColor("Olive",   "#808000"),
        NamedColor("Coral",   "#FF7F50"),
        NamedColor("Salmon",  "#FA8072"),
        NamedColor("Gold",    "#FFD700"),
        NamedColor("Khaki",   "#F0E68C"),
        NamedColor("Crimson", "#DC143C"),
        NamedColor("Lavender","#E6E6FA"),
        NamedColor("Beige",   "#F5F5DC"),
    )

    // Pre-compute LAB values for all named colors
    val namedColorsLab = namedColors.map { color ->
        val (r, g, b) = hexToRgb(color.hex)
        Pair(color.name, rgbToLab(r, g, b))
    }

    val (r, g, b) = hexToRgb(hex)
    val inputLab = rgbToLab(r, g, b)

    return namedColorsLab.minByOrNull { (_, lab) ->
        val (l1, a1, b1) = inputLab
        val (l2, a2, b2) = lab
        sqrt((l1 - l2).pow(2) + (a1 - a2).pow(2) + (b1 - b2).pow(2))
    }?.first ?: "Unknown"
}