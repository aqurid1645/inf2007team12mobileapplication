package com.inf2007team12mobileapplication.di

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.inf2007team12mobileapplication.data.Repo
import com.inf2007team12mobileapplication.data.RepoImpt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    @Provides
    @ViewModelScoped
    fun providesFirebaseAuth()  = FirebaseAuth.getInstance()

    @Provides
    @ViewModelScoped
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth,scanner: GmsBarcodeScanner,firebaseFirestore: FirebaseFirestore): Repo {
        return RepoImpt(firebaseAuth,scanner,firebaseFirestore)
    }
    @ViewModelScoped
    @Provides
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }
    @Provides
    @ViewModelScoped
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @ViewModelScoped
    @Provides
    fun provideBarCodeOptions() : GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    }

    @ViewModelScoped
    @Provides
    fun provideBarCodeScanner(context: Context, options: GmsBarcodeScannerOptions): GmsBarcodeScanner {
        return GmsBarcodeScanning.getClient(context, options)
    }
}