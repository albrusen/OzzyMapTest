package com.example.ozzymaptest.navigation

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


object AppDestinations {
    const val HOME_ROUTE = "home"
    const val MAP_ROUTE = "map"
    const val LAT_ARG = "minLat"
    const val LON_ARG = "maxLon"
    const val INITIAL_ZOOM_ARG = "initialZoom"

    const val MAP_ROUTE_WITH_ARGS = "${MAP_ROUTE}?" +
            "${LAT_ARG}={${LAT_ARG}}&" +
            "${LON_ARG}={${LON_ARG}}&" +
            "${INITIAL_ZOOM_ARG}={${INITIAL_ZOOM_ARG}}"

    val MAP_ARGS = listOf(
        navArgument(LAT_ARG) {
            type = NavType.StringType
            nullable = false
        },
        navArgument(LON_ARG) {
            type = NavType.StringType
            nullable = false
        },
        navArgument(INITIAL_ZOOM_ARG) {
            type = NavType.StringType
            nullable = false
        }
    )

    fun mapRouteWithCoords(
        lat: Double,
        lon: Double,
        initialZoom: Float? = null
    ): String {
        val encodedLat = URLEncoder.encode(lat.toString(), StandardCharsets.UTF_8.toString())
        val encodedLon = URLEncoder.encode(lon.toString(), StandardCharsets.UTF_8.toString())
        val zoom = URLEncoder.encode(initialZoom.toString(), StandardCharsets.UTF_8.toString())

        return "$MAP_ROUTE?" +
                "$LAT_ARG=$encodedLat&" +
                "$LON_ARG=$encodedLon&" +
                "$INITIAL_ZOOM_ARG=$zoom"
    }

}

interface MapFeatureNavigator {
    fun navigateToMap(
        lat: Double,
        lon: Double,
        initialZoom: Float?
    )
}

class MapFeatureNavigatorImpl (
    private val navController: NavController
) : MapFeatureNavigator {

    override fun navigateToMap(
        lat: Double,
        lon: Double,
        initialZoom: Float?
    ) {
        val route = AppDestinations.mapRouteWithCoords(lat, lon, initialZoom)
        navController.navigate(route)
    }
}