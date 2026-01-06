package com.example.timecalculator.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timecalculator.navigation.Home
import com.example.timecalculator.navigation.PermissionGuide
import com.example.timecalculator.service.TimeReaderAccessibilityService
import com.example.timecalculator.ui.components.MainNavDrawer
import com.example.timecalculator.ui.components.MainTopBar
import com.example.timecalculator.ui.screen.HomeScreen
import com.example.timecalculator.ui.screen.PermissionGuideScreen
import com.example.timecalculator.ui.theme.TimeCalculatorTheme
import com.example.timecalculator.viewmodel.AppThemeViewModel
import com.example.timecalculator.viewmodel.PermissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissionViewModel: PermissionViewModel by viewModels()

    // 悬浮窗权限请求
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val granted = checkOverlayPermission()
        permissionViewModel.setOverlayPermission(granted)
    }

    // 电池优化白名单请求
    private val batteryOptimizationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val granted = checkBatteryOptimization()
        permissionViewModel.setBatteryOptimization(granted)
    }

    // 无障碍服务设置返回
    private val accessibilitySettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        refreshAllPermissions()
    }

    // 通知权限请求 (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionViewModel.setNotificationPermission(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")

        enableEdgeToEdge()
        setContent {
            val themeViewModel: AppThemeViewModel = hiltViewModel()

            val darkMode by themeViewModel.darkMode.collectAsState()
            val themeSeq by themeViewModel.themeSequence.collectAsState()
            val contrast by themeViewModel.contrast.collectAsState()
            val permissionState by permissionViewModel.uiState.collectAsState()

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            // 根据权限状态决定起始页面
            val startDestination = if (permissionState.allRequiredGranted) Home else PermissionGuide

            TimeCalculatorTheme(
                darkMode = darkMode,
                themeSequence = themeSeq,
                contrast = contrast
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    // 权限引导页
                    composable<PermissionGuide> {
                        PermissionGuideScreen(
                            viewModel = permissionViewModel,
                            onRequestOverlayPermission = { requestOverlayPermission() },
                            onRequestBatteryOptimization = { requestBatteryOptimization() },
                            onRequestNotificationPermission = { requestNotificationPermission() },
                            onRequestAccessibilityPermission = { openAccessibilitySettings() },
                            onAllPermissionsGranted = {
                                navController.navigate(Home) {
                                    popUpTo(PermissionGuide) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 主页
                    composable<Home> {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = { MainNavDrawer() }
                        ) {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    MainTopBar(
                                        onClickNavIcon = {
                                            scope.launch {
                                                drawerState.apply { if (isClosed) open() else close() }
                                            }
                                        }
                                    )
                                },
                                bottomBar = {},
                            ) { innerPadding ->
                                HomeScreen(
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
        refreshAllPermissions()
    }

    /**
     * 刷新所有权限状态并更新到 ViewModel
     */
    private fun refreshAllPermissions() {
        val overlay = checkOverlayPermission()
        val battery = checkBatteryOptimization()
        val notification = checkNotificationPermission()
        val accessibility = checkAccessibilityPermission()
        Log.d("MainActivity", "Permissions: 悬浮窗权限:$overlay, 电池优化白名单:$battery, 通知权限:$notification, 无障碍服务:$accessibility")

        permissionViewModel.setAllPermissions(
            overlay = overlay,
            battery = battery,
            notification = notification,
            accessibility = accessibility
        )
    }

    // ==================== 权限检查方法 ====================

    /**
     * 检查悬浮窗权限
     */
    private fun checkOverlayPermission(): Boolean {
        Log.d("MainActivity", "checkOverlayPermission")
        return Settings.canDrawOverlays(this)
    }

    /**
     * 检查电池优化白名单
     */
    private fun checkBatteryOptimization(): Boolean {
        Log.d("MainActivity", "checkBatteryOptimization")
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    /**
     * 检查通知权限
     */
    private fun checkNotificationPermission(): Boolean {
        Log.d("MainActivity", "checkNotificationPermission")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * 检查无障碍服务是否开启
     */
    private fun checkAccessibilityPermission(): Boolean {
        Log.d("MainActivity", "checkAccessibilityPermission")
        val serviceName = "${packageName}/${TimeReaderAccessibilityService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        
        return enabledServices.contains(serviceName)
    }

    // ==================== 权限请求方法 ====================

    /**
     * 请求悬浮窗权限
     */
    private fun requestOverlayPermission() {
        Log.d("MainActivity", "requestOverlayPermission")
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        overlayPermissionLauncher.launch(intent)
    }

    /**
     * 请求忽略电池优化（后台运行权限）
     */
    @Suppress("BatteryLife")
    private fun requestBatteryOptimization() {
        Log.d("MainActivity", "requestBatteryOptimization")
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
        batteryOptimizationLauncher.launch(intent)
    }

    /**
     * 请求通知权限
     */
    private fun requestNotificationPermission() {
        Log.d("MainActivity", "requestNotificationPermission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * 打开无障碍服务设置
     */
    private fun openAccessibilitySettings() {
        Log.d("MainActivity", "openAccessibilitySettings")
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        accessibilitySettingsLauncher.launch(intent)
    }
}