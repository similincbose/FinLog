package me.riafy.finlog.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Single entry point for dependency injection, called from BaseApp on Android
 * and from the iOS app delegate.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    appDeclaration()
    modules(
        platformModule,
        dataModule,
        viewModelModule
    )
}
