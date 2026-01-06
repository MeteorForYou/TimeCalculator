package com.example.timecalculator.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepository @Inject constructor(
  private val prefs: SharedPreferences
) {
  fun getDarkMode() = prefs.getInt("dark_mode", 1)
  fun saveDarkMode(value: Int) = prefs.edit { putInt("dark_mode", value) }

  fun getThemeSequence() = prefs.getInt("themeSequence", 1)
  fun saveThemeSequence(value: Int) = prefs.edit { putInt("themeSequence", value) }

  fun getContrast() = prefs.getInt("contrast", 1)
  fun saveContrast(value: Int) = prefs.edit { putInt("contrast", value) }
}