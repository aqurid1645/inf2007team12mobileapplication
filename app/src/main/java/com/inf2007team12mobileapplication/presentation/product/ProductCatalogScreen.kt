package com.inf2007team12mobileapplication.presentation.product

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import com.inf2007team12mobileapplication.R

@Composable
fun ProductCatalogScreen(
    navController: NavController,
    viewModel: ProductCatalogViewModel = hiltViewModel()
) {
    val products by viewModel.products.observeAsState(emptyList())

    LazyColumn(
        contentPadding = PaddingValues(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product, onBorrowClick = {
                // Define what happens when you click 'Start borrowing'
                // For example, you might navigate to a product detail screen:
                // navController.navigate("productDetail/${product.id}")
                navController.navigate("camera")

            }, onSearchClick = {
                // Define what happens when you click 'Search'
                // For example, you might navigate to a search screen:
                // navController.navigate("search")
                navController.navigate("inventory/${product.productName}")
            })
        }
    }
}

@Composable
fun ProductCard(
    product: ProductCatalog,
    onBorrowClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_raspberry_pi), // Use the actual drawable resource ID for your image
                contentDescription = "Product Image",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = product.productName ?: "Product Name",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = product.productDescription ?: "Product Description",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onBorrowClick /*nav to camera screen */,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("Borrow", modifier = Modifier.padding(8.dp))
                }
                Button(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Search", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}