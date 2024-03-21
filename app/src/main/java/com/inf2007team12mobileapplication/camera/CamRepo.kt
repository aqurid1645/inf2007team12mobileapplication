package com.example.camera

import kotlinx.coroutines.flow.Flow


interface CamRepo {

    fun startScanning(): Flow<String?>
}