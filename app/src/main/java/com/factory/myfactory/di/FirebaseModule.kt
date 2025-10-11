package com.factory.myfactory.di

import com.factory.myfactory.data.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // NOTE: You do NOT need to provide AuthRepository here if AuthRepository
    // has an @Inject constructor that takes FirebaseAuth & FirebaseFirestore.
    // If you prefer to provide it here explicitly, you can — but I recommend
    // using @Inject on the repository constructor (cleaner).
}
