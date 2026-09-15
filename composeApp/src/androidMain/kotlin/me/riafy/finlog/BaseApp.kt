package me.riafy.finlog

import android.app.Application
import me.riafy.finlog.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BaseApp)
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
        }
    }
}
