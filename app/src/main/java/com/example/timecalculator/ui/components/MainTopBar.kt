package com.example.timecalculator.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.timecalculator.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(onClickNavIcon: () -> Unit) {
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = {
      Text(
        "时间计算器",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    },
    navigationIcon = {
      IconButton(onClick = onClickNavIcon) {
        Icon(
          painter = painterResource(R.drawable.topbar_menu),
          contentDescription = "抽屉导航"
        )
      }
    },
    scrollBehavior = scrollBehavior,
  )
}