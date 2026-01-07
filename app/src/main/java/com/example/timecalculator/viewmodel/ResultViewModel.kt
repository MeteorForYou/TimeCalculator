package com.example.timecalculator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.timecalculator.utils.TimeParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "ResultViewModel"
    }

    private val _parsedTimes = MutableStateFlow<List<TimeParser.ParsedTime>>(emptyList())
    val parsedTimes: StateFlow<List<TimeParser.ParsedTime>> = _parsedTimes.asStateFlow()

    /**
     * 添加时间到结果列表（自动去重）
     * 根据时间戳去重，避免重复添加相同的时间
     */
    fun addResult(allTimes: List<TimeParser.ParsedTime>) {
        val currentTimes = _parsedTimes.value
        val uniqueTimes = (currentTimes + allTimes).distinctBy { it.timeMillis }
        _parsedTimes.value = uniqueTimes
        Log.d(TAG, "添加 ${allTimes.size} 个时间，去重后共 ${uniqueTimes.size} 个")
    }

    /**
     * 清空结果
     */
    fun clearResult() {
        Log.d(TAG, "清空结果")
        _parsedTimes.value = emptyList()
    }
}