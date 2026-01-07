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
        Log.d(TAG, "init")
        setFloatingWindowState(FloatingWindowService.isRunning)
    }

    /**
     * 启动悬浮窗服务
     */
    fun startFloatingWindow() {
        Log.d(TAG, "startFloatingWindow")
        val context = getApplication<Application>()
        val intent = Intent(context, FloatingWindowService::class.java)
        context.startService(intent)
        // 立即更新状态，不等待服务异步启动
        setFloatingWindowState(true)
    }

    /**
     * 停止悬浮窗服务
     */
    fun stopFloatingWindow() {
        Log.d(TAG, "stopFloatingWindow")
        val context = getApplication<Application>()
        val intent = Intent(context, FloatingWindowService::class.java)
        context.stopService(intent)
        // 立即更新状态，不等待服务异步停止
        setFloatingWindowState(false)
    }

    /**
     * 更新悬浮窗状态
     */
    fun setFloatingWindowState(state: Boolean) {
        _isFloatingWindowRunning.value = state
        Log.d(TAG, "setFloatingWindowState 悬浮窗状态: $state")
    }
}

