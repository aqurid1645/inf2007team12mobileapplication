package com.inf2007team12mobileapplication.di

import com.google.firebase.auth.FirebaseAuth
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
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth): Repo {
        return RepoImpt(firebaseAuth)
    }

}