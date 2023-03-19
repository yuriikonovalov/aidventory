package com.aidventory.core.common.di

import javax.inject.Qualifier

/**
 * A qualifier for injecting a MainActivity::class.java as Class<ComponentActivity>.
 *
 * A dagger module with a 'provide' method is defined in the ActivityModule class in the app module.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainActivityClass
