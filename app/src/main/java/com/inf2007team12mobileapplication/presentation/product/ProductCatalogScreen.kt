@file:JvmName("ProductCatalogKt")

package com.inf2007team12mobileapplication.presentation.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.inf2007team12mobileapplication.R
import com.inf2007team12mobileapplication.presentation.product.ProductCatalog
import com.inf2007team12mobileapplication.presentation.product.ProductCatalogViewModel


val products = listOf(
    ProductCatalog("Raspberry Pi", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
    ProductCatalog("chicken", "A credit card sized computer", R.drawable.ic_raspberry_pi),
)

@Composable
fun ProductCatalogScreen(navController: NavController, viewModel: ProductCatalogViewModel = hiltViewModel()) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(navController, product)
        }
    }
}

@Composable
fun ProductCard(navController: NavController,product: ProductCatalog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = "${product.name} image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {   navController.navigate("camera") },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Start borrowing")
            }
        }
    }
}
