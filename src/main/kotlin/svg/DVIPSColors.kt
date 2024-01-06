package svg

import java.awt.Color

/**
 * Provides the 68 standard colors known to dvips.
 * Usage of these colors in the graphics together with the xcolor package in LaTeX
 * ```\usepackage[dvipsnames]{xcolor}```
 * ensures correct color matching between LaTeX generated content and svg graphics content.
 *
 * See [WikiBooks LaTeX/Colors](https://en.wikibooks.org/wiki/LaTeX/Colors) for more information.
 */
object DVIPSColors {
    /**
     * @return 100% transparency by setting alpha = 0
     */
    val Transparent = Color(255, 255, 255, 0)

    /**
     * @return DVIPS color named Apricot `#FBB982` with alpha parameter set to max value (no transparency)
     */
    val Apricot = Color(251, 185, 130, 255)

    /**
     * @return DVIPS color named Aquamarine `#00B5BE` with alpha parameter set to max value (no transparency)
     */
    val Aquamarine = Color(0, 181, 190, 255)

    /**
     * @return DVIPS color named Bittersweet `#C04F17` with alpha parameter set to max value (no transparency)
     */
    val Bittersweet = Color(192, 79, 23, 255)

    /**
     * @return DVIPS color named Black `#221E1F` with alpha parameter set to max value (no transparency)
     */
    val Black = Color(34, 30, 31, 255)

    /**
     * @return DVIPS color named Blue `#2D2F92` with alpha parameter set to max value (no transparency)
     */
    val Blue = Color(45, 47, 146, 255)

    /**
     * @return DVIPS color named BlueGreen `#00B3B8` with alpha parameter set to max value (no transparency)
     */
    val BlueGreen = Color(0, 179, 184, 255)

    /**
     * @return DVIPS color named BlueViolet `#473992` with alpha parameter set to max value (no transparency)
     */
    val BlueViolet = Color(71, 57, 146, 255)

    /**
     * @return DVIPS color named BrickRed `#B6321C` with alpha parameter set to max value (no transparency)
     */
    val BrickRed = Color(182, 50, 28, 255)

    /**
     * @return DVIPS color named Brown `#792500` with alpha parameter set to max value (no transparency)
     */
    val Brown = Color(121, 37, 0, 255)

    /**
     * @return DVIPS color named BurntOrange `#F7921D` with alpha parameter set to max value (no transparency)
     */
    val BurntOrange = Color(247, 146, 29, 255)

    /**
     * @return DVIPS color named CadetBlue `#74729A` with alpha parameter set to max value (no transparency)
     */
    val CadetBlue = Color(116, 114, 154, 255)

    /**
     * @return DVIPS color named CarnationPink `#F282B4` with alpha parameter set to max value (no transparency)
     */
    val CarnationPink = Color(242, 130, 180, 255)

    /**
     * @return DVIPS color named Cerulean `#00A2E3` with alpha parameter set to max value (no transparency)
     */
    val Cerulean = Color(0, 162, 227, 255)

    /**
     * @return DVIPS color named CornflowerBlue `#41B0E4` with alpha parameter set to max value (no transparency)
     */
    val CornflowerBlue = Color(65, 176, 228, 255)

    /**
     * @return DVIPS color named Cyan `#00AEEF` with alpha parameter set to max value (no transparency)
     */
    val Cyan = Color(0, 174, 239, 255)

    /**
     * @return DVIPS color named Dandelion `#FDBC42` with alpha parameter set to max value (no transparency)
     */
    val Dandelion = Color(253, 188, 66, 255)

    /**
     * @return DVIPS color named DarkOrchid `#A4538A` with alpha parameter set to max value (no transparency)
     */
    val DarkOrchid = Color(164, 83, 138, 255)

    /**
     * @return DVIPS color named Emerald `#00A99D` with alpha parameter set to max value (no transparency)
     */
    val Emerald = Color(0, 169, 157, 255)

    /**
     * @return DVIPS color named ForestGreen `#009B55` with alpha parameter set to max value (no transparency)
     */
    val ForestGreen = Color(0, 155, 85, 255)

    /**
     * @return DVIPS color named Fuchsia `#8C368C` with alpha parameter set to max value (no transparency)
     */
    val Fuchsia = Color(140, 54, 140, 255)

    /**
     * @return DVIPS color named Goldenrod `#FFDF42` with alpha parameter set to max value (no transparency)
     */
    val Goldenrod = Color(255, 223, 66, 255)

    /**
     * @return DVIPS color named Gray `#949698` with alpha parameter set to max value (no transparency)
     */
    val Gray = Color(148, 150, 152, 255)

    /**
     * @return DVIPS color named Green `#00A64F` with alpha parameter set to max value (no transparency)
     */
    val Green = Color(0, 166, 79, 255)

    /**
     * @return DVIPS color named GreenYellow `#DFE674` with alpha parameter set to max value (no transparency)
     */
    val GreenYellow = Color(223, 230, 116, 255)

    /**
     * @return DVIPS color named JungleGreen `#00A99A` with alpha parameter set to max value (no transparency)
     */
    val JungleGreen = Color(0, 169, 154, 255)

    /**
     * @return DVIPS color named Lavender `#F49EC4` with alpha parameter set to max value (no transparency)
     */
    val Lavender = Color(244, 158, 196, 255)

    /**
     * @return DVIPS color named LimeGreen `#8DC73E` with alpha parameter set to max value (no transparency)
     */
    val LimeGreen = Color(141, 199, 62, 255)

    /**
     * @return DVIPS color named Magenta `#EC008C` with alpha parameter set to max value (no transparency)
     */
    val Magenta = Color(236, 0, 140, 255)

    /**
     * @return DVIPS color named Mahogany `#A9341F` with alpha parameter set to max value (no transparency)
     */
    val Mahogany = Color(169, 52, 31, 255)

    /**
     * @return DVIPS color named Maroon `#AF3235` with alpha parameter set to max value (no transparency)
     */
    val Maroon = Color(175, 50, 53, 255)

    /**
     * @return DVIPS color named Melon `#F89E7B` with alpha parameter set to max value (no transparency)
     */
    val Melon = Color(248, 158, 123, 255)

    /**
     * @return DVIPS color named MidnightBlue `#006795` with alpha parameter set to max value (no transparency)
     */
    val MidnightBlue = Color(0, 103, 149, 255)

    /**
     * @return DVIPS color named Mulberry `#A93C93` with alpha parameter set to max value (no transparency)
     */
    val Mulberry = Color(169, 60, 147, 255)

    /**
     * @return DVIPS color named NavyBlue `#006EB8` with alpha parameter set to max value (no transparency)
     */
    val NavyBlue = Color(0, 110, 184, 255)

    /**
     * @return DVIPS color named OliveGreen `#3C8031` with alpha parameter set to max value (no transparency)
     */
    val OliveGreen = Color(60, 128, 49, 255)

    /**
     * @return DVIPS color named Orange `#F58137` with alpha parameter set to max value (no transparency)
     */
    val Orange = Color(245, 129, 55, 255)

    /**
     * @return DVIPS color named OrangeRed `#ED135A` with alpha parameter set to max value (no transparency)
     */
    val OrangeRed = Color(237, 19, 90, 255)

    /**
     * @return DVIPS color named Orchid `#AF72B0` with alpha parameter set to max value (no transparency)
     */
    val Orchid = Color(175, 114, 176, 255)

    /**
     * @return DVIPS color named Peach `#F7965A` with alpha parameter set to max value (no transparency)
     */
    val Peach = Color(247, 150, 90, 255)

    /**
     * @return DVIPS color named Periwinkle `#7977B8` with alpha parameter set to max value (no transparency)
     */
    val Periwinkle = Color(121, 119, 184, 255)

    /**
     * @return DVIPS color named PineGreen `#008B72` with alpha parameter set to max value (no transparency)
     */
    val PineGreen = Color(0, 139, 114, 255)

    /**
     * @return DVIPS color named Plum `#92268F` with alpha parameter set to max value (no transparency)
     */
    val Plum = Color(146, 38, 143, 255)

    /**
     * @return DVIPS color named ProcessBlue `#00B0F0` with alpha parameter set to max value (no transparency)
     */
    val ProcessBlue = Color(0, 176, 240, 255)

    /**
     * @return DVIPS color named Purple `#99479B` with alpha parameter set to max value (no transparency)
     */
    val Purple = Color(153, 71, 155, 255)

    /**
     * @return DVIPS color named RawSienna `#974006` with alpha parameter set to max value (no transparency)
     */
    val RawSienna = Color(151, 64, 6, 255)

    /**
     * @return DVIPS color named Red `#ED1B23` with alpha parameter set to max value (no transparency)
     */
    val Red = Color(237, 27, 35, 255)

    /**
     * @return DVIPS color named RedOrange `#F26035` with alpha parameter set to max value (no transparency)
     */
    val RedOrange = Color(242, 96, 53, 255)

    /**
     * @return DVIPS color named RedViolet `#A1246B` with alpha parameter set to max value (no transparency)
     */
    val RedViolet = Color(161, 36, 107, 255)

    /**
     * @return DVIPS color named Rhodamine `#EF559F` with alpha parameter set to max value (no transparency)
     */
    val Rhodamine = Color(239, 85, 159, 255)

    /**
     * @return DVIPS color named RoyalBlue `#0071BC` with alpha parameter set to max value (no transparency)
     */
    val RoyalBlue = Color(0, 113, 188, 255)

    /**
     * @return DVIPS color named RoyalPurple `#613F99` with alpha parameter set to max value (no transparency)
     */
    val RoyalPurple = Color(97, 63, 153, 255)

    /**
     * @return DVIPS color named RubineRed `#ED017D` with alpha parameter set to max value (no transparency)
     */
    val RubineRed = Color(237, 1, 125, 255)

    /**
     * @return DVIPS color named Salmon `#F69289` with alpha parameter set to max value (no transparency)
     */
    val Salmon = Color(246, 146, 137, 255)

    /**
     * @return DVIPS color named SeaGreen `#3FBC9D` with alpha parameter set to max value (no transparency)
     */
    val SeaGreen = Color(63, 188, 157, 255)

    /**
     * @return DVIPS color named Sepia `#671800` with alpha parameter set to max value (no transparency)
     */
    val Sepia = Color(103, 24, 0, 255)

    /**
     * @return DVIPS color named SkyBlue `#46C5DD` with alpha parameter set to max value (no transparency)
     */
    val SkyBlue = Color(70, 197, 221, 255)

    /**
     * @return DVIPS color named SpringGreen `#C6DC67` with alpha parameter set to max value (no transparency)
     */
    val SpringGreen = Color(198, 220, 103, 255)

    /**
     * @return DVIPS color named Tan `#DA9D76` with alpha parameter set to max value (no transparency)
     */
    val Tan = Color(218, 157, 118, 255)

    /**
     * @return DVIPS color named TealBlue `#00AEB3` with alpha parameter set to max value (no transparency)
     */
    val TealBlue = Color(0, 174, 179, 255)

    /**
     * @return DVIPS color named Thistle `#D883B7` with alpha parameter set to max value (no transparency)
     */
    val Thistle = Color(216, 131, 183, 255)

    /**
     * @return DVIPS color named Turquoise `#00B4CE` with alpha parameter set to max value (no transparency)
     */
    val Turquoise = Color(0, 180, 206, 255)

    /**
     * @return DVIPS color named Violet `#58429B` with alpha parameter set to max value (no transparency)
     */
    val Violet = Color(88, 66, 155, 255)

    /**
     * @return DVIPS color named VioletRed `#EF58A0` with alpha parameter set to max value (no transparency)
     */
    val VioletRed = Color(239, 88, 160, 255)

    /**
     * @return DVIPS color named White `#FFFFFF` with alpha parameter set to max value (no transparency)
     */
    val White = Color(255, 255, 255, 255)

    /**
     * @return DVIPS color named WildStrawberry `#EE2967` with alpha parameter set to max value (no transparency)
     */
    val WildStrawberry = Color(238, 41, 103, 255)

    /**
     * @return DVIPS color named Yellow `#FFF200` with alpha parameter set to max value (no transparency)
     */
    val Yellow = Color(255, 242, 0, 255)

    /**
     * @return DVIPS color named YellowGreen `#98CC70` with alpha parameter set to max value (no transparency)
     */
    val YellowGreen = Color(152, 204, 112, 255)

    /**
     * @return DVIPS color named YellowOrange `#FAA21A` with alpha parameter set to max value (no transparency)
     */
    val YellowOrange = Color(250, 162, 26, 255)

    /**
     * The 68 colors of DVIPS in alphabetical order with alpha parameter set to max value (no transparency).
     */
    val palette = listOf(
        Apricot,
        Aquamarine,
        Bittersweet,
        Black,
        Blue,
        BlueGreen,
        BlueViolet,
        BrickRed,
        Brown,
        BurntOrange,
        CadetBlue,
        CarnationPink,
        Cerulean,
        CornflowerBlue,
        Cyan,
        Dandelion,
        DarkOrchid,
        Emerald,
        ForestGreen,
        Fuchsia,
        Goldenrod,
        Gray,
        Green,
        GreenYellow,
        JungleGreen,
        Lavender,
        LimeGreen,
        Magenta,
        Mahogany,
        Maroon,
        Melon,
        MidnightBlue,
        Mulberry,
        NavyBlue,
        OliveGreen,
        Orange,
        OrangeRed,
        Orchid,
        Peach,
        Periwinkle,
        PineGreen,
        Plum,
        ProcessBlue,
        Purple,
        RawSienna,
        Red,
        RedOrange,
        RedViolet,
        Rhodamine,
        RoyalBlue,
        RoyalPurple,
        RubineRed,
        Salmon,
        SeaGreen,
        Sepia,
        SkyBlue,
        SpringGreen,
        Tan,
        TealBlue,
        Thistle,
        Turquoise,
        Violet,
        VioletRed,
        White,
        WildStrawberry,
        Yellow,
        YellowGreen,
        YellowOrange,
    )

    /**
     * Extension function to set a specified alpha value.
     * Creates a new instance of [Color].
     *
     * @param alpha the new alpha value
     *
     * @return a new instance of [Color] with the same RGB values as [this] and its alpha value set to the given [alpha].
     */
    fun Color.withAlpha(alpha: Int): Color {
        return Color(this.red, this.green, this.blue, alpha)
    }
}