package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object HospitalDetail : Screen("hospital_detail/{hospitalId}") {
        fun createRoute(hospitalId: String): String = "hospital_detail/$hospitalId"
    }
    data object Settings : Screen("settings")
    data object PrivacyPolicy : Screen("privacy_policy")
    data object TermsOfUse : Screen("terms_of_use")
    data object About : Screen("about")
    data object Contact : Screen("contact")
}
