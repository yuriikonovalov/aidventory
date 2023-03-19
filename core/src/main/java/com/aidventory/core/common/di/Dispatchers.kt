package com.aidventory.core.common.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: AppDispatcher)

enum class AppDispatcher {
    IO
}