package com.nicos.eyedropper_api.utils

import java.util.Locale
import kotlin.math.pow

/** Formats an ARGB int as #RRGGBB (alpha dropped). */
fun Int.toHexCode(): String = String.format(Locale.ROOT, "#%06X", this and 0xFFFFFF)

/** Parses "#RRGGBB", "RRGGBB" or "#AARRGGBB" into an int (alpha ignored later). */
fun String.hexToColorInt(): Int = trimStart('#').toLong(16).toInt()

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

    val l = (116.0 * fy) - 16.0
    val a = 500.0 * (fx - fy)
    val bLab = 200.0 * (fy - fz)

    return Triple(l, a, bLab)
}

/** Converts an ARGB (or RGB) int straight to Lab, ignoring alpha. */
fun Int.toLab(): Triple<Double, Double, Double> =
    rgbToLab((this shr 16) and 0xFF, (this shr 8) and 0xFF, this and 0xFF)

data class NamedColor(val name: String, val rgb: Int)

/**
 * All 148 CSS/X11 named colours, minus exact duplicates
 * (Aqua = Cyan, Fuchsia = Magenta, and the "Grey" spellings of "Gray").
 */
private val namedColors = listOf(
    NamedColor("Alice Blue",             0xF0F8FF),
    NamedColor("Antique White",          0xFAEBD7),
    NamedColor("Aquamarine",             0x7FFFD4),
    NamedColor("Azure",                  0xF0FFFF),
    NamedColor("Beige",                  0xF5F5DC),
    NamedColor("Bisque",                 0xFFE4C4),
    NamedColor("Black",                  0x000000),
    NamedColor("Blanched Almond",        0xFFEBCD),
    NamedColor("Blue",                   0x0000FF),
    NamedColor("Blue Violet",            0x8A2BE2),
    NamedColor("Brown",                  0xA52A2A),
    NamedColor("Burly Wood",             0xDEB887),
    NamedColor("Cadet Blue",             0x5F9EA0),
    NamedColor("Chartreuse",             0x7FFF00),
    NamedColor("Chocolate",              0xD2691E),
    NamedColor("Coral",                  0xFF7F50),
    NamedColor("Cornflower Blue",        0x6495ED),
    NamedColor("Cornsilk",               0xFFF8DC),
    NamedColor("Crimson",                0xDC143C),
    NamedColor("Cyan",                   0x00FFFF),
    NamedColor("Dark Blue",              0x00008B),
    NamedColor("Dark Cyan",              0x008B8B),
    NamedColor("Dark Goldenrod",         0xB8860B),
    NamedColor("Dark Gray",              0xA9A9A9),
    NamedColor("Dark Green",             0x006400),
    NamedColor("Dark Khaki",             0xBDB76B),
    NamedColor("Dark Magenta",           0x8B008B),
    NamedColor("Dark Olive Green",       0x556B2F),
    NamedColor("Dark Orange",            0xFF8C00),
    NamedColor("Dark Orchid",            0x9932CC),
    NamedColor("Dark Red",               0x8B0000),
    NamedColor("Dark Salmon",            0xE9967A),
    NamedColor("Dark Sea Green",         0x8FBC8F),
    NamedColor("Dark Slate Blue",        0x483D8B),
    NamedColor("Dark Slate Gray",        0x2F4F4F),
    NamedColor("Dark Turquoise",         0x00CED1),
    NamedColor("Dark Violet",            0x9400D3),
    NamedColor("Deep Pink",              0xFF1493),
    NamedColor("Deep Sky Blue",          0x00BFFF),
    NamedColor("Dim Gray",               0x696969),
    NamedColor("Dodger Blue",            0x1E90FF),
    NamedColor("Firebrick",              0xB22222),
    NamedColor("Floral White",           0xFFFAF0),
    NamedColor("Forest Green",           0x228B22),
    NamedColor("Gainsboro",              0xDCDCDC),
    NamedColor("Ghost White",            0xF8F8FF),
    NamedColor("Gold",                   0xFFD700),
    NamedColor("Goldenrod",              0xDAA520),
    NamedColor("Gray",                   0x808080),
    NamedColor("Green",                  0x008000),
    NamedColor("Green Yellow",           0xADFF2F),
    NamedColor("Honeydew",               0xF0FFF0),
    NamedColor("Hot Pink",               0xFF69B4),
    NamedColor("Indian Red",             0xCD5C5C),
    NamedColor("Indigo",                 0x4B0082),
    NamedColor("Ivory",                  0xFFFFF0),
    NamedColor("Khaki",                  0xF0E68C),
    NamedColor("Lavender",               0xE6E6FA),
    NamedColor("Lavender Blush",         0xFFF0F5),
    NamedColor("Lawn Green",             0x7CFC00),
    NamedColor("Lemon Chiffon",          0xFFFACD),
    NamedColor("Light Blue",             0xADD8E6),
    NamedColor("Light Coral",            0xF08080),
    NamedColor("Light Cyan",             0xE0FFFF),
    NamedColor("Light Goldenrod Yellow", 0xFAFAD2),
    NamedColor("Light Gray",             0xD3D3D3),
    NamedColor("Light Green",            0x90EE90),
    NamedColor("Light Pink",             0xFFB6C1),
    NamedColor("Light Salmon",           0xFFA07A),
    NamedColor("Light Sea Green",        0x20B2AA),
    NamedColor("Light Sky Blue",         0x87CEFA),
    NamedColor("Light Slate Gray",       0x778899),
    NamedColor("Light Steel Blue",       0xB0C4DE),
    NamedColor("Light Yellow",           0xFFFFE0),
    NamedColor("Lime",                   0x00FF00),
    NamedColor("Lime Green",             0x32CD32),
    NamedColor("Linen",                  0xFAF0E6),
    NamedColor("Magenta",                0xFF00FF),
    NamedColor("Maroon",                 0x800000),
    NamedColor("Medium Aquamarine",      0x66CDAA),
    NamedColor("Medium Blue",            0x0000CD),
    NamedColor("Medium Orchid",          0xBA55D3),
    NamedColor("Medium Purple",          0x9370DB),
    NamedColor("Medium Sea Green",       0x3CB371),
    NamedColor("Medium Slate Blue",      0x7B68EE),
    NamedColor("Medium Spring Green",    0x00FA9A),
    NamedColor("Medium Turquoise",       0x48D1CC),
    NamedColor("Medium Violet Red",      0xC71585),
    NamedColor("Midnight Blue",          0x191970),
    NamedColor("Mint Cream",             0xF5FFFA),
    NamedColor("Misty Rose",             0xFFE4E1),
    NamedColor("Moccasin",               0xFFE4B5),
    NamedColor("Navajo White",           0xFFDEAD),
    NamedColor("Navy",                   0x000080),
    NamedColor("Old Lace",               0xFDF5E6),
    NamedColor("Olive",                  0x808000),
    NamedColor("Olive Drab",             0x6B8E23),
    NamedColor("Orange",                 0xFFA500),
    NamedColor("Orange Red",             0xFF4500),
    NamedColor("Orchid",                 0xDA70D6),
    NamedColor("Pale Goldenrod",         0xEEE8AA),
    NamedColor("Pale Green",             0x98FB98),
    NamedColor("Pale Turquoise",         0xAFEEEE),
    NamedColor("Pale Violet Red",        0xDB7093),
    NamedColor("Papaya Whip",            0xFFEFD5),
    NamedColor("Peach Puff",             0xFFDAB9),
    NamedColor("Peru",                   0xCD853F),
    NamedColor("Pink",                   0xFFC0CB),
    NamedColor("Plum",                   0xDDA0DD),
    NamedColor("Powder Blue",            0xB0E0E6),
    NamedColor("Purple",                 0x800080),
    NamedColor("Rebecca Purple",         0x663399),
    NamedColor("Red",                    0xFF0000),
    NamedColor("Rosy Brown",             0xBC8F8F),
    NamedColor("Royal Blue",             0x4169E1),
    NamedColor("Saddle Brown",           0x8B4513),
    NamedColor("Salmon",                 0xFA8072),
    NamedColor("Sandy Brown",            0xF4A460),
    NamedColor("Sea Green",              0x2E8B57),
    NamedColor("Seashell",               0xFFF5EE),
    NamedColor("Sienna",                 0xA0522D),
    NamedColor("Silver",                 0xC0C0C0),
    NamedColor("Sky Blue",               0x87CEEB),
    NamedColor("Slate Blue",             0x6A5ACD),
    NamedColor("Slate Gray",             0x708090),
    NamedColor("Snow",                   0xFFFAFA),
    NamedColor("Spring Green",           0x00FF7F),
    NamedColor("Steel Blue",             0x4682B4),
    NamedColor("Tan",                    0xD2B48C),
    NamedColor("Teal",                   0x008080),
    NamedColor("Thistle",                0xD8BFD8),
    NamedColor("Tomato",                 0xFF6347),
    NamedColor("Turquoise",              0x40E0D0),
    NamedColor("Violet",                 0xEE82EE),
    NamedColor("Wheat",                  0xF5DEB3),
    NamedColor("White",                  0xFFFFFF),
    NamedColor("White Smoke",            0xF5F5F5),
    NamedColor("Yellow",                 0xFFFF00),
    NamedColor("Yellow Green",           0x9ACD32),
)

// Computed once, on first use, instead of on every call.
private val namedColorsLab: List<Pair<String, Triple<Double, Double, Double>>> by lazy {
    namedColors.map { it.name to it.rgb.toLab() }
}

/** Nearest named colour by Euclidean distance in Lab space (CIE76). */
fun getClosestColorName(color: Int): String {
    val (l1, a1, b1) = color.toLab()
    // Squared distance is enough for comparison; no need for sqrt.
    return namedColorsLab.minByOrNull { (_, lab) ->
        val (l2, a2, b2) = lab
        (l1 - l2).pow(2) + (a1 - a2).pow(2) + (b1 - b2).pow(2)
    }?.first ?: "Unknown"
}

/** Convenience overload for hex strings such as "#FF5733" or "#80FF5733". */
fun getClosestColorName(hex: String): String = getClosestColorName(hex.hexToColorInt())