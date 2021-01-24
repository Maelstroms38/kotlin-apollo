package com.example.justdesserts.androidApp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.justdesserts.shared.cache.DessertAction
import com.example.justdesserts.androidApp.ui.desserts.detail.DessertDetailView
import com.example.justdesserts.androidApp.ui.desserts.favorites.FavoriteListView
import com.example.justdesserts.androidApp.ui.desserts.form.DessertFormView
import com.example.justdesserts.androidApp.ui.desserts.list.DessertListView
import com.example.justdesserts.shared.cache.Dessert
import com.example.justdesserts.shared.cache.toQueryString

sealed class Screens(val route: String, val label: String, val icon: ImageVector? = null) {
    object DessertsScreen : Screens("Desserts", "Desserts", Icons.Default.List)
    object DessertDetailsScreen : Screens("DessertDetails", "DessertDetails")
    object DessertFormScreen : Screens("DessertForm", "DessertForm")
    object FavoritesScreen : Screens("Favorites", "Favorites", Icons.Default.Favorite)
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MainLayout()
            }
        }
    }
}

@Composable
fun MainLayout() {
    val navController = rememberNavController()

    val bottomNavigationItems = listOf(Screens.DessertsScreen, Screens.FavoritesScreen)
    val bottomBar: @Composable () -> Unit = { BottomNavigation(navController, bottomNavigationItems) }
    val queryString = "?id={id}&userId={userId}&name={name}&description={description}&imageUrl={imageUrl}"

    NavHost(navController, startDestination = Screens.DessertsScreen.route) {
        composable(Screens.DessertsScreen.route) {
            DessertListView(bottomBar, newDessertSelected = {
                navController.navigate(Screens.DessertFormScreen.route)
            }, dessertSelected = {
                navController.navigate(Screens.DessertDetailsScreen.route +
                        it.toQueryString())
            })
        }
        composable(Screens.DessertDetailsScreen.route + queryString,
            arguments = listOf(
            navArgument("id") { defaultValue = "" },
            navArgument("userId") { defaultValue = "" },
            navArgument("name") { defaultValue = "" },
            navArgument("description") { defaultValue = "" },
            navArgument("imageUrl") { defaultValue = "" })) { backStackEntry ->

            val id = backStackEntry.arguments?.get("id") as String
            val userId = backStackEntry.arguments?.get("userId") as String
            val name = backStackEntry.arguments?.get("name") as String
            val description = backStackEntry.arguments?.get("description") as String
            val imageUrl = backStackEntry.arguments?.get("imageUrl") as String
            val dessert = Dessert(id = id, userId = userId, name = name, description = description, imageUrl = imageUrl)

            DessertDetailView(dessert,
                editDessertSelected = {
                    navController.navigate(Screens.DessertFormScreen.route +
                            it.toQueryString())
                }, popBack = { navController.popBackStack() })
        }
        
        composable(Screens.DessertFormScreen.route + queryString,
            arguments = listOf(
                navArgument("id") { defaultValue = "new" },
                navArgument("userId") { defaultValue = "" },
                navArgument("name") { defaultValue = "" },
                navArgument("description") { defaultValue = "" },
                navArgument("imageUrl") { defaultValue = "" })) { backStackEntry ->

            val id = backStackEntry.arguments?.get("id") as String
            val userId = backStackEntry.arguments?.get("userId") as String
            val name = backStackEntry.arguments?.get("name") as String
            val description = backStackEntry.arguments?.get("description") as String
            val imageUrl = backStackEntry.arguments?.get("imageUrl") as String
            val action = if (id != "new") DessertAction.UPDATE else DessertAction.CREATE
            val dessert = Dessert(id = id, userId = userId, name = name, description = description, imageUrl = imageUrl)

            DessertFormView(dessert, action,
                popBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screens.FavoritesScreen.route) {
            FavoriteListView(bottomBar, dessertSelected = {
                navController.navigate(Screens.DessertDetailsScreen.route +
                        it.toQueryString())
            })
        }
    }
}


@Composable
private fun BottomNavigation(
    navController: NavHostController,
    items: List<Screens>
) {
    BottomNavigation {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { screen.icon?.let { Icon(screen.icon) } },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    // This if check gives us a "singleTop" behavior where we do not create a
                    // second instance of the composable if we are already on that destination
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }

}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
}
