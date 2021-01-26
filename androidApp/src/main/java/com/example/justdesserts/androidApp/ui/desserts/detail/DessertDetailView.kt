package com.example.justdesserts.androidApp.ui.desserts.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.justdesserts.shared.cache.Dessert
import com.example.justdesserts.shared.cache.Review
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.async
import org.koin.androidx.compose.getViewModel

@Composable
fun DessertDetailView(dessertId: String, editDessertSelected: (dessertId: String) -> Unit, popBack: () -> Unit) {
    val dessertDetailViewModel = getViewModel<DessertDetailViewModel>()
    val (dessert, setDessert) = remember { mutableStateOf(Dessert("", "", "", "", "")) }
    val (reviews, setReviews) = remember { mutableStateOf(emptyList<Review>()) }
    val (isFavorite, setIsFavorite) = remember { mutableStateOf(false) }

    LaunchedEffect(dessertId) {
        val isFavorite = dessertDetailViewModel.isFavorite(dessertId)
        setIsFavorite(isFavorite)
        try {
            val readDessert = dessertDetailViewModel.getDessert(dessertId)
            readDessert?.let {
                setDessert(it.dessert)
                setReviews(it.reviews)
            } ?: run {
                if (isFavorite) {
                    dessertDetailViewModel.removeFavorite(dessertId)
                }
                popBack()
            }
        } catch(err: Exception) {
            print(err.message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dessert.name) },
                navigationIcon = {
                    IconButton(onClick = { popBack() }) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Toggle Favorite
                        if (isFavorite) {
                            dessertDetailViewModel.removeFavorite(dessertId)
                        } else {
                            dessertDetailViewModel.saveFavorite(dessert)
                        }
                        setIsFavorite(!isFavorite)
                    }) {
                        if (isFavorite) {
                            Icon(Icons.Outlined.Delete)
                        } else {
                            Icon(Icons.Filled.Favorite)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editDessertSelected(dessertId)
            }, backgroundColor = MaterialTheme.colors.primary) {
                Icon(Icons.Outlined.Create)
            }
        },
        bodyContent = {
            Surface(color = Color.White) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Surface(color = Color.White) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val imageUrl = dessert.imageUrl
                            Card(
                                modifier = Modifier.preferredSize(150.dp),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                CoilImage(data = imageUrl, contentScale = ContentScale.Crop)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.preferredHeight(16.dp))

                    Text(
                        "Summary", style = typography.h5, color = AmbientContentColor.current,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    Text(
                        dessert.description, style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.preferredHeight(16.dp))

                    Text(
                        "Reviews", style = typography.h5, color = AmbientContentColor.current,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    Surface(color = Color.White) {
                        DessertReviewsList(reviews)
                    }
                }
            }
        }
    )
}

@Composable
private fun DessertReviewsList(reviews: List<Review>) {

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        reviews.let { reviewsList ->
            LazyColumn {
                items(reviewsList) { item ->
                    item?.let { review ->
                        Text(
                            review.text,
                            style = typography.h6
                        )
                        Row {
                            List(review.rating.toInt()) {
                                Icon(Icons.Filled.Star)
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    }
}
