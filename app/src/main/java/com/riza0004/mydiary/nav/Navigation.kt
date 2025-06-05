package com.riza0004.mydiary.nav

sealed class Screen(val route: String){
    data object Home: Screen("mainScreen")
    data object Form: Screen("formScreen")
}