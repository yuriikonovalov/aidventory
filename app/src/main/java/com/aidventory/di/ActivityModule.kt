package com.aidventory.di

import androidx.activity.ComponentActivity
import com.aidventory.core.common.di.MainActivityClass
import com.aidventory.presentation.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ActivityModule {

    /*
    * This MainActivity::class.java is used for creating a pending intent for an expired supply notification.
    * The notification is created in NotifySupplyExpiredWorker.
    * MainActivity::class.java is injected in the constructor of the worker class.
    * */
    @Suppress("UNCHECKED_CAST")
    @MainActivityClass
    @Provides
    fun providesMainActivityClass(): Class<ComponentActivity> =
        MainActivity::class.java as Class<ComponentActivity>
}