package com.example.timecalculator.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.timecalculator.service.FloatingWindowService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FloatingWindowViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _isFloatingWindowRunning = MutableStateFlow(false)
    val isFloatingWindowRunning: StateFlow<Boolean> = _isFloatingWindowRunning.asStateFlow()

    companion object {
        private const val TAG = "FloatingWindowVM"
    }

    init {
        updateFloatingWindowState()
    }

    /**
     * 启动悬浮窗服务
     */
    fun startFloatingWindow() {
        Log.d(TAG, "启动悬浮窗服务")
        val context = getApplication<Application>()
        val intent = Intent(context, FloatingWindowService::class.java)
        context.startService(intent)
        updateFloatingWindowState()
    }

    /**
     * 停止悬浮窗服务
     */
    fun stopFloatingWindow() {
        Log.d(TAG, "停止悬浮窗服务")
        val context = getApplication<Application>()
        val intent = Intent(context, FloatingWindowService::class.java)
        context.stopService(intent)
        updateFloatingWindowState()
    }

    /**
     * 更新悬浮窗状态
     */
    fun updateFloatingWindowState() {
        _isFloatingWindowRunning.value = FloatingWindowService.isRunning
        Log.d(TAG, "悬浮窗状态: ${_isFloatingWindowRunning.value}")
    }
}

