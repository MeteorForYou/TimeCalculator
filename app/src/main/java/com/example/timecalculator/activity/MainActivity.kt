package com.example.timecalculator.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timecalculator.ui.components.MainNavDrawer
import com.example.timecalculator.ui.components.MainTopBar
import com.example.timecalculator.ui.screen.HomeScreen
import com.example.timecalculator.ui.theme.TimeCalculatorTheme
import com.example.timecalculator.viewmodel.AppThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object Home

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")

        enableEdgeToEdge()
        setContent {
            val themeViewModel: AppThemeViewModel = hiltViewModel()
            val darkMode by themeViewModel.darkMode.collectAsState()
            val themeSeq by themeViewModel.themeSequence.collectAsState()
            val contrast by themeViewModel.contrast.collectAsState()

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            TimeCalculatorTheme(
                darkMode = darkMode,
                themeSequence = themeSeq,
                contrast = contrast
            ) {
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
                            ) },
                        bottomBar = {},
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Home,
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            composable<Home> {
                                HomeScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}