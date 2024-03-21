package com.example.camera

import android.app.Application
import android.content.Context
import android.view.View
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@InstallIn(SingletonComponent::class)
@Module
object AppModule {


    @Singleton
    @Provides
    fun provideContext(app:Application):Context{
        return app.applicationContext
    }

    @Singleton
    @Provides
    fun provideBarCodeOptions() : GmsBarcodeScannerOptions{
        return GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    }

    @Singleton
    @Provides
    fun provideBarCodeScanner(context: Context,options: GmsBarcodeScannerOptions):GmsBarcodeScanner{
        return GmsBarcodeScanning.getClient(context, options)
    }
    @Provides
    @Singleton
    fun providesRepositoryImpl(scanner: GmsBarcodeScanner):CamRepo{
        return CamRepoImpt(scanner)
    }
}