package com.example.timecalculator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.timecalculator.repository.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class AppThemeViewModel @Inject constructor(
  private val repository: PrefsRepository
) : ViewModel() {
  init {
    Log.d("AppThemeViewModel", "AppThemeViewModel has been created")
  }
  private val _darkMode = MutableStateFlow(repository.getDarkMode())
  val darkMode: StateFlow<Int> = _darkMode
  fun setDarkMode(value: Int) {
    _darkMode.value = value
    repository.saveDarkMode(value)
    Log.d("AppThemeViewModel","setDarkMode darkMode:${darkMode.value}")
  }

  private val _themeSequence = MutableStateFlow(repository.getThemeSequence())
  val themeSequence: StateFlow<Int> = _themeSequence
  fun setThemeSequence(value: Int) {
    _themeSequence.value = value
    repository.saveThemeSequence(value)
    Log.d("AppThemeViewModel","setThemeSequence themeSequence:${themeSequence.value}")
  }

  private val _contrast = MutableStateFlow(repository.getContrast())
  val contrast: StateFlow<Int> = _contrast
  fun setContrast(value: Int) {
    _contrast.value = value
    repository.saveContrast(value)
    Log.d("AppThemeViewModel","setContrast contrast:${contrast}")
  }
}