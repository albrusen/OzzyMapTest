package com.example.ozzymaptest.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.HomeScreen
import com.example.presentation.mapscreen.MapScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val mapFeatureNavigator = remember(navController) {
        MapFeatureNavigatorImpl(navController)
    }

    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_ROUTE,
        modifier = modifier
    ) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(
                onItemClick = { lat, lon, initialZoom ->
                    mapFeatureNavigator.navigateToMap(lat, lon, initialZoom)
                }
            )
        }
        composable(
            route = "${AppDestinations.MAP_ROUTE}?" +
                    "${AppDestinations.LAT_ARG}={${AppDestinations.LAT_ARG}}&" +
                    "${AppDestinations.LON_ARG}={${AppDestinations.LON_ARG}}&" +
                    "${AppDestinations.INITIAL_ZOOM_ARG}={${AppDestinations.INITIAL_ZOOM_ARG}}",
            arguments = listOf(
                navArgument(AppDestinations.LAT_ARG) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(AppDestinations.LON_ARG) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(AppDestinations.INITIAL_ZOOM_ARG) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString(AppDestinations.LAT_ARG)?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString(AppDestinations.LON_ARG)?.toDoubleOrNull()
            val initialZoom = backStackEntry.arguments?.getString(AppDestinations.INITIAL_ZOOM_ARG)?.toFloatOrNull()
            Log.d("AppNavHost", "AppNavHost: MapScreen")
            if (lat != null && lon != null) {
                MapScreen(
                    lat = lat,
                    lon = lon,
                    initialZoom = initialZoom ?: 15f,
                )
            } else {
                MapScreen()
            }
        }
    }
}