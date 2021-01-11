package com.example.justdesserts.androidApp.ui.desserts.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.justdesserts.androidApp.models.Dessert
import com.example.justdesserts.androidApp.models.DessertAction
import org.koin.androidx.compose.getViewModel

@Composable
fun DessertListView(bottomBar: @Composable () -> Unit, newDessertSelected: (dessert: Dessert) -> Unit, dessertSelected: (dessert: Dessert) -> Unit) {
  val dessertListViewModel = getViewModel<DessertListViewModel>()
  val lazyDessertList = dessertListViewModel.desserts.collectAsLazyPagingItems()

  Scaffold(
    topBar = { TopAppBar(title = { Text( "Desserts") }) },
    bottomBar = bottomBar,
    floatingActionButton = {
      FloatingActionButton(onClick = {
        newDessertSelected(Dessert(DessertAction.CREATE, "", "", "", ""))
      }, backgroundColor = MaterialTheme.colors.primary) {
        Icon(Icons.Outlined.Add)
      }
    },
    bodyContent = {
      LazyColumn(contentPadding = it) {
        items(lazyDessertList) { dessert ->
          val readDessert = Dessert(
            dessertId = dessert?.id ?: "",
            name = dessert?.name ?: "",
            description = dessert?.description ?: "",
            imageUrl = dessert?.imageUrl ?: ""
          )
          DessertListRowView(readDessert, dessertSelected)
        }
      }
    })
}