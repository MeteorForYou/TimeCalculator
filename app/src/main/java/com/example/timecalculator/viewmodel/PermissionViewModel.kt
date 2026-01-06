package com.example.timecalculator.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PermissionUiState(
    val hasOverlayPermission: Boolean = false,
    val hasBatteryOptimization: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false
) {
    // 必需权限：悬浮窗 + 电池优化 + 无障碍服务（核心功能）
    val allRequiredGranted: Boolean
        get() = hasOverlayPermission && hasBatteryOptimization && hasAccessibilityPermission

    val allGranted: Boolean
        get() = hasOverlayPermission && hasBatteryOptimization && hasNotificationPermission && hasAccessibilityPermission
}

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState())
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    /**
     * 更新悬浮窗权限状态
     */
    fun setOverlayPermission(granted: Boolean) {
        _uiState.update { it.copy(hasOverlayPermission = granted) }
    }

    /**
     * 更新电池优化状态
     */
    fun setBatteryOptimization(granted: Boolean) {
        _uiState.update { it.copy(hasBatteryOptimization = granted) }
    }

    /**
     * 更新通知权限状态
     */
    fun setNotificationPermission(granted: Boolean) {
        _uiState.update { it.copy(hasNotificationPermission = granted) }
    }

    /**
     * 更新无障碍权限状态
     */
    fun setAccessibilityPermission(granted: Boolean) {
        _uiState.update { it.copy(hasAccessibilityPermission = granted) }
    }

    /**
     * 批量更新所有权限状态
     */
    fun setAllPermissions(
        overlay: Boolean,
        battery: Boolean,
        notification: Boolean,
        accessibility: Boolean
    ) {
        _uiState.update {
            it.copy(
                hasOverlayPermission = overlay,
                hasBatteryOptimization = battery,
                hasNotificationPermission = notification,
                hasAccessibilityPermission = accessibility
            )
        }
    }
}
