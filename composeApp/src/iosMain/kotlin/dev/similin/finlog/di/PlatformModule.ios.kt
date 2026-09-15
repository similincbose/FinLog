package dev.similin.finlog.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import dev.similin.finlog.database.FinlogDatabase
import dev.similin.finlog.utils.image.ImagePicker
import dev.similin.finlog.utils.image.IosImagePicker
import dev.similin.finlog.utils.receipt.IosTextRecognizer
import dev.similin.finlog.utils.receipt.ReceiptTextRecognizer
import dev.similin.finlog.utils.share.IosShareService
import dev.similin.finlog.utils.share.ShareService
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {

    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }

    single<SqlDriver> { NativeSqliteDriver(FinlogDatabase.Schema, "finlog.db") }

    single<ImagePicker> { IosImagePicker() }

    single<ReceiptTextRecognizer> { IosTextRecognizer() }

    single<ShareService> { IosShareService() }
}
