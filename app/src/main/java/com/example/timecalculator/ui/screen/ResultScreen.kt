package com.example.timecalculator.ui.screen

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timecalculator.utils.TimeParser
import com.example.timecalculator.viewmodel.FloatingWindowViewModel
import com.example.timecalculator.viewmodel.ResultViewModel

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    floatingWindowViewModel: FloatingWindowViewModel = hiltViewModel(),
    resultViewModel: ResultViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val parsedTimes by resultViewModel.parsedTimes.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 返回HomeScreen
            ElevatedButton(onClick = { resultViewModel.clearResult() }) {
                Text("清空")
            }
            // 开启悬浮窗
            ElevatedButton(onClick = {
                resultViewModel.clearResult()
                floatingWindowViewModel.startFloatingWindow()
                // 将APP移到后台
                (context as? Activity)?.moveTaskToBack(true)
            }) {
                Text("清空并重新获取")
            }
            ElevatedButton(onClick = {
                floatingWindowViewModel.startFloatingWindow()
                // 将APP移到后台
                (context as? Activity)?.moveTaskToBack(true)
            }) {
                Text("添加")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 显示解析到的时间列表
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "解析到 ${parsedTimes.size} 个时间：",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    parsedTimes.forEach { time ->
                        TimeItem(time = time)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {  }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TimeItem(time: TimeParser.ParsedTime) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time.displayText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}