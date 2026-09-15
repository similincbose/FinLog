package me.riafy.finlog.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import me.riafy.finlog.database.FinlogDatabase
import me.riafy.finlog.utils.image.ImagePicker
import me.riafy.finlog.utils.image.IosImagePicker
import me.riafy.finlog.utils.receipt.IosTextRecognizer
import me.riafy.finlog.utils.receipt.ReceiptTextRecognizer
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {

    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }

    single<SqlDriver> { NativeSqliteDriver(FinlogDatabase.Schema, "finlog.db") }

    single<ImagePicker> { IosImagePicker() }

    single<ReceiptTextRecognizer> { IosTextRecognizer() }
}
