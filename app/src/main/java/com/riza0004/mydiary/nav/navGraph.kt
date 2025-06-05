package com.riza0004.mydiary.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.riza0004.mydiary.ui.screen.FormScreen
import com.riza0004.mydiary.ui.screen.MainScreen

@Composable
fun SetupNavGraph(navHostController: NavHostController = rememberNavController() ){
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route){
            MainScreen(navHostController = navHostController)
        }
        composable(route = Screen.Form.route){
            FormScreen(navHostController = navHostController)
        }
    }
}