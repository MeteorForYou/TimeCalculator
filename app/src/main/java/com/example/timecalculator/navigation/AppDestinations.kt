package com.example.timecalculator.navigation

import kotlinx.serialization.Serializable

/**
 * 应用导航目的地定义
 */

@Serializable
data object PermissionGuide

@Serializable
data object Home

@Serializable
data object History

@Serializable
data class SessionDetail(val sessionId: Long)

@Serializable
data class CalculationDetail(val calculationId: Long)

@Serializable
data object Settings

