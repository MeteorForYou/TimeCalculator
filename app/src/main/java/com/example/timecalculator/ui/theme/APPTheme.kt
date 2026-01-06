package com.example.timecalculator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.timecalculator.ui.theme.first.FirstTheme
import com.example.timecalculator.ui.theme.fourth.FourthTheme
import com.example.timecalculator.ui.theme.second.SecondTheme
import com.example.timecalculator.ui.theme.third.ThirdTheme

data class ThemeSet(
  val light: ColorScheme,
  val dark: ColorScheme,
  val mediumLight: ColorScheme,
  val mediumDark: ColorScheme,
  val highLight: ColorScheme,
  val highDark: ColorScheme
)

val FirstThemeSet = ThemeSet(
  light = FirstTheme.lightScheme,
  dark = FirstTheme.darkScheme,
  mediumLight = FirstTheme.mediumContrastLightColorScheme,
  mediumDark = FirstTheme.mediumContrastDarkColorScheme,
  highLight = FirstTheme.highContrastLightColorScheme,
  highDark = FirstTheme.highContrastDarkColorScheme
)

val SecondThemeSet = ThemeSet(
  light = SecondTheme.lightScheme,
  dark = SecondTheme.darkScheme,
  mediumLight = SecondTheme.mediumContrastLightColorScheme,
  mediumDark = SecondTheme.mediumContrastDarkColorScheme,
  highLight = SecondTheme.highContrastLightColorScheme,
  highDark = SecondTheme.highContrastDarkColorScheme
)

val ThirdThemeSet = ThemeSet(
  light = ThirdTheme.lightScheme,
  dark = ThirdTheme.darkScheme,
  mediumLight = ThirdTheme.mediumContrastLightColorScheme,
  mediumDark = ThirdTheme.mediumContrastDarkColorScheme,
  highLight = ThirdTheme.highContrastLightColorScheme,
  highDark = ThirdTheme.highContrastDarkColorScheme
)

val FourthThemeSet = ThemeSet(
  light = FourthTheme.lightScheme,
  dark = FourthTheme.darkScheme,
  mediumLight = FourthTheme.mediumContrastLightColorScheme,
  mediumDark = FourthTheme.mediumContrastDarkColorScheme,
  highLight = FourthTheme.highContrastLightColorScheme,
  highDark = FourthTheme.highContrastDarkColorScheme
)

val themeSets = mapOf(
  1 to FirstThemeSet,
  2 to SecondThemeSet,
  3 to ThirdThemeSet,
  4 to FourthThemeSet
)

fun ThemeSet.pickContrast(isDark: Boolean, contrast: Int): ColorScheme {
  return when (contrast) {
    1 -> if (isDark) dark else light
    2 -> if (isDark) mediumDark else mediumLight
    else -> if (isDark) highDark else highLight
  }
}

fun resolveTheme(
  darkTheme: Boolean,
  themeSequence: Int,
  contrast: Int
): ColorScheme {
  val themeSet = themeSets[themeSequence] ?: FirstThemeSet
  return themeSet.pickContrast(darkTheme, contrast)
}

@Composable
fun TimeCalculatorTheme(
  darkMode: Int = 1,      //FollowSystem, Light, Dark
  themeSequence: Int = 1, //First, Second, Third, Fourth
  contrast: Int = 1,      //Default, Medium, High
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val darkTheme: Boolean = when(darkMode){
    1 -> isSystemInDarkTheme()
    2 -> false
    else -> true
  }

  // 动态颜色优先
  if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val context = LocalContext.current
    val scheme = if (darkTheme) dynamicDarkColorScheme(context)
    else dynamicLightColorScheme(context)

    MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
    return
  }

  val colorScheme = resolveTheme(darkTheme, themeSequence, contrast)

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}