package com.example.timecalculator.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.timecalculator.activity.MainActivity
import com.example.timecalculator.ui.theme.TimeCalculatorTheme

/**
 * 悬浮窗服务
 */
class FloatingWindowService : Service(), ViewModelStoreOwner, SavedStateRegistryOwner {

    companion object {
        private const val TAG = "FloatingWindowService"
        var isRunning = false
            private set
    }

    private lateinit var windowManager: WindowManager
    private var floatingView: ComposeView? = null
    private var params: WindowManager.LayoutParams? = null
    
    // 用于拖动的变量
    private var offsetX = 0f
    private var offsetY = 0f

    // Lifecycle相关
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "悬浮窗服务创建")
        
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createFloatingWindow()
        isRunning = true
        
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * 创建悬浮窗
     */
    private fun createFloatingWindow() {
        // 悬浮窗布局参数
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        // 创建ComposeView
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingWindowService)
            setViewTreeViewModelStoreOwner(this@FloatingWindowService)
            setViewTreeSavedStateRegistryOwner(this@FloatingWindowService)
            
            setContent {
                TimeCalculatorTheme {
                    FloatingWindowContent(
                        onReadTimeClick = { readTimeAndReturnToApp() },
                        onDragStart = {
                            offsetX = 0f
                            offsetY = 0f
                        },
                        onDrag = { dragX, dragY ->
                            // 累积偏移量
                            offsetX += dragX
                            offsetY += dragY
                            // 更新悬浮窗位置
                            params!!.x += dragX.toInt()
                            params!!.y += dragY.toInt()
                            windowManager.updateViewLayout(floatingView, params)
                        }
                    )
                }
            }
        }

        // 添加悬浮窗到窗口管理器
        windowManager.addView(floatingView, params)
        Log.d(TAG, "悬浮窗已显示")
    }


    /**
     * 读取时间并返回APP
     */
    private fun readTimeAndReturnToApp() {
        // 读取屏幕文字
        val accessibilityService = TimeReaderAccessibilityService.instance
        if (accessibilityService != null) {
            val screenText = accessibilityService.readScreenText()

            // 跳转回APP
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("screen_text", screenText)
            }
            startActivity(intent)
            
            // 关闭悬浮窗服务
            stopSelf()
        } else {
            Log.e(TAG, "无障碍服务未开启或未运行")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
        }
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        isRunning = false
        Log.d(TAG, "悬浮窗服务已销毁")
    }
}

/**
 * 悬浮窗Compose内容
 */
@Composable
private fun FloatingWindowContent(
    onReadTimeClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
    ) {
        Button(
            onClick = onReadTimeClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "获取时间",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}