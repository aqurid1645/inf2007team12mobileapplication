package com.example.camera

import androidx.compose.foundation.layout.*

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {

    val state = viewModel.state.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "hello")
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(0.5f), contentAlignment = Alignment.Center) {
            Text(text =  state.value.details)
            
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .weight(0.5f), contentAlignment = Alignment.BottomCenter) {
            Button(onClick = { viewModel.startScanning() }) {
                Text(text = "start scanning")
            }
        }


    }


}