package com.example.timecalculator.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timecalculator.R
import com.example.timecalculator.viewmodel.PermissionViewModel

@Composable
fun PermissionGuideScreen(
    modifier: Modifier = Modifier,
    viewModel: PermissionViewModel = hiltViewModel(),
    onRequestOverlayPermission: () -> Unit,
    onRequestBatteryOptimization: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestAccessibilityPermission: () -> Unit,
    onAllPermissionsGranted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 标题
        Text(
            text = "权限设置",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "为了让应用正常运行，需要以下权限",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 悬浮窗权限
        PermissionItem(
            title = "悬浮窗权限",
            description = "用于显示悬浮按钮，方便快速读取屏幕时间",
            isGranted = uiState.hasOverlayPermission,
            onRequestPermission = onRequestOverlayPermission
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 电池优化白名单
        PermissionItem(
            title = "后台运行权限",
            description = "允许应用在后台持续运行，确保悬浮窗服务不被系统杀死",
            isGranted = uiState.hasBatteryOptimization,
            onRequestPermission = onRequestBatteryOptimization
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 无障碍服务权限
        PermissionItem(
            title = "无障碍服务",
            description = "用于读取屏幕上显示的时间信息",
            isGranted = uiState.hasAccessibilityPermission,
            onRequestPermission = onRequestAccessibilityPermission
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 通知权限（可选）
        PermissionItem(
            title = "通知权限",
            description = "用于在后台运行时显示通知（可选）",
            isGranted = uiState.hasNotificationPermission,
            onRequestPermission = onRequestNotificationPermission,
            isOptional = true
        )

        Spacer(modifier = Modifier.weight(1f))

        // 继续按钮
        Button(
            onClick = onAllPermissionsGranted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.allRequiredGranted,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (uiState.allRequiredGranted) "开始使用" else "请先获取必要权限",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PermissionItem(
    title: String,
    description: String,
    isGranted: Boolean,
    onRequestPermission: () -> Unit,
    isOptional: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isGranted) 0.6f else 1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态图标
            Icon(
                imageVector = if (isGranted) Icons.Filled.CheckCircle else Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isGranted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 文字信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isOptional) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "可选",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 按钮
            Button(
                onClick = onRequestPermission,
                enabled = !isGranted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGranted)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isGranted) "已获取" else "前往获取",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

