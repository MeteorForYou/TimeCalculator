package com.example.timecalculator.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.timecalculator.ui.theme.themeSets
import com.example.timecalculator.viewmodel.AppThemeViewModel


val darkModes = mapOf(
  1 to "跟随系统",
  2 to "浅色模式",
  3 to "暗夜模式",
)
val contrasts = mapOf(
  1 to "默认",
  2 to "中",
  3 to "高",
)

@Composable
fun MainNavDrawer(themeViewModel: AppThemeViewModel = hiltViewModel()) {
  val darkMode by themeViewModel.darkMode.collectAsState()
  val themeSeq by themeViewModel.themeSequence.collectAsState()
  val contrast by themeViewModel.contrast.collectAsState()
  ModalDrawerSheet(
    modifier = Modifier
      .fillMaxHeight()
      .fillMaxWidth(0.75f),
    drawerContainerColor = MaterialTheme.colorScheme.surface,
  ) {
    ElevatedCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp, start = 12.dp, end = 12.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary
      ),
      elevation = CardDefaults.cardElevation(
        defaultElevation = 6.dp
      ),
    ) {
      Column(modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)) {
        Text("暗夜模式")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
              color = Color.Gray.copy(alpha = 0.2f),
              shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .padding(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          darkModes.forEach { (index,text)->
            DrawerSelectableBox(
              selected = darkMode == index,
              onClick = { themeViewModel.setDarkMode(index) },
              modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
            ) {
              Text(text = text)
            }
          }
        }
      }

      Column(modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)){
        Text("颜\u3000\u3000色")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .height(40.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          themeSets.forEach { (index, theme) ->
            val selected = themeSeq == index
            val borderColor by animateColorAsState(
              if (selected) MaterialTheme.colorScheme.surfaceBright else Color.Transparent,
              label = "themeBorder"
            )
            Surface(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { themeViewModel.setThemeSequence(index) },
              shape = RoundedCornerShape(20.dp),
              color = theme.dark.primary,
              border = BorderStroke(2.dp, borderColor)
            ) { }
          }
        }
      }

      Column(modifier = Modifier.padding(8.dp)){
        Text("对\u2002比\u2002度")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
              color = Color.Gray.copy(alpha = 0.2f),
              shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .padding(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          contrasts.forEach { (index,text)->
            DrawerSelectableBox(
              selected = contrast  == index,
              onClick = { themeViewModel.setContrast(index) },
              modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
            ) {
              Text(text = text)
            }
          }
        }
      }
    }
  }
}

@Composable
fun DrawerSelectableBox(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val bgColor by animateColorAsState(
    targetValue = if (selected)
      Color.White.copy(alpha = 0.2f)
    else
      Color.Transparent,
    label = "bgColor"
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(
        color = bgColor,
        shape = RoundedCornerShape(20.dp))
      .clickable{ onClick() },
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}