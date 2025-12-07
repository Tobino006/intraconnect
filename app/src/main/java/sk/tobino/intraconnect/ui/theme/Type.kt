package sk.tobino.intraconnect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import sk.tobino.intraconnect.R

val Poppins = FontFamily (
    Font (
        resId = R.font.poppins_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font (
        resId = R.font.poppins_bold,
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    ),
    Font (
        resId = R.font.poppins_bold_italic,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
    Font (
        resId = R.font.poppins_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    )
)

val AppTypography = Typography (
    bodyLarge = TextStyle (
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineLarge = TextStyle (
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp
    )
)