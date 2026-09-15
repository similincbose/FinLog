package me.riafy.finlog.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import me.riafy.finlog.database.FinlogDatabase
import me.riafy.finlog.utils.image.AndroidImagePicker
import me.riafy.finlog.utils.image.ImagePicker
import me.riafy.finlog.utils.receipt.AndroidTextRecognizer
import me.riafy.finlog.utils.receipt.ReceiptTextRecognizer
import me.riafy.finlog.utils.share.AndroidShareService
import me.riafy.finlog.utils.share.ShareService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {

    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences("finlog_prefs", android.content.Context.MODE_PRIVATE)
        )
    }

    single<SqlDriver> {
        AndroidSqliteDriver(FinlogDatabase.Schema, androidContext(), "finlog.db")
    }

    single { AndroidImagePicker(context = androidContext()) }

    single<ImagePicker> { get<AndroidImagePicker>() }

    single<ReceiptTextRecognizer> { AndroidTextRecognizer() }

    single<ShareService> { AndroidShareService(context = androidContext()) }
}
