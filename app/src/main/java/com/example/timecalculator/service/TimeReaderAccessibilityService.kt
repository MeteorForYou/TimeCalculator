package com.example.timecalculator.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 无障碍服务 - 用于读取屏幕上的时间信息
 */
class TimeReaderAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "TimeReaderService"

        // 服务是否正在运行
        var isRunning = false
            private set

        // 服务实例（用于外部调用）
        var instance: TimeReaderAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true
        instance = this
        Log.d(TAG, "无障碍服务已连接")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 暂时不处理事件，后续实现屏幕读取功能
    }

    override fun onInterrupt() {
        Log.d(TAG, "无障碍服务被中断")
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        instance = null
        Log.d(TAG, "无障碍服务已销毁")
    }

    /**
     * 读取当前屏幕上的所有文本
     */
    fun readScreenText(): String {
        val rootNode = rootInActiveWindow ?: return ""
        val texts = mutableListOf<String>()
        collectTexts(rootNode, texts)
        rootNode.recycle()
        return texts.joinToString("\n")
    }

    /**
     * 递归收集节点中的文本
     */
    private fun collectTexts(node: AccessibilityNodeInfo, texts: MutableList<String>) {
        // 获取节点文本
        node.text?.toString()?.let { text ->
            if (text.isNotBlank()) {
                texts.add(text)
            }
        }

        // 获取内容描述
        node.contentDescription?.toString()?.let { desc ->
            if (desc.isNotBlank() && !texts.contains(desc)) {
                texts.add(desc)
            }
        }

        // 递归处理子节点
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectTexts(child, texts)
            child.recycle()
        }
    }
}

