package com.example.timecalculator.ui.screen

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timecalculator.ui.components.MainNavDrawer
import com.example.timecalculator.ui.components.MainTopBar
import com.example.timecalculator.ui.theme.TimeCalculatorTheme
import com.example.timecalculator.viewmodel.AppThemeViewModel
import com.example.timecalculator.viewmodel.FloatingWindowViewModel
import com.example.timecalculator.viewmodel.ResultViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    themeVM: AppThemeViewModel = hiltViewModel(),
    floatingWindowVM: FloatingWindowViewModel = hiltViewModel(),
    resultVM: ResultViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isFloatingWindowRunning by floatingWindowVM.isFloatingWindowRunning.collectAsState()
    val parsedTimes by resultVM.parsedTimes.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { MainNavDrawer(themeVM) }
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
            // 如果有解析结果，显示ResultScreen
            if (parsedTimes.isNotEmpty()) {
                ResultScreen(
                    modifier = Modifier.padding(innerPadding),
                    floatingWindowViewModel = floatingWindowVM,
                    resultViewModel = resultVM
                )
            }
            else {
                // 否则显示HomeScreen
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(48.dp))

                    // 使用说明卡片
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "使用指南",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "1. 点击下方按钮启动悬浮窗\n" +
                                  "2. 打开需要读取时间的页面\n" +
                                  "3. 点击悬浮窗按钮 → 读取时间\n" +
                                  "4. 拖动悬浮窗 → 调整位置\n" +
                                  "5. APP会自动读取屏幕文字并返回",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 启动/停止按钮
                    Button(
                        onClick = {
                            if (isFloatingWindowRunning) {
                                floatingWindowVM.stopFloatingWindow()
                            } else {
                                floatingWindowVM.startFloatingWindow()
                                // 将APP移到后台
                                (context as? Activity)?.moveTaskToBack(true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = if (isFloatingWindowRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = if (isFloatingWindowRunning) "停止悬浮窗" else "启动悬浮窗",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true,name = "light")
@Preview(showBackground = true,name = "dark",
  uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
  TimeCalculatorTheme {
  }
}