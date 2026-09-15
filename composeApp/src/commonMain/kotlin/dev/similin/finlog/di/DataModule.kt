package dev.similin.finlog.di

import dev.similin.finlog.data.local.preference.AppPreference
import dev.similin.finlog.data.repo.CategoryRepository
import dev.similin.finlog.data.repo.ExpenseRepository
import dev.similin.finlog.data.repo.PaymentMethodRepository
import dev.similin.finlog.database.FinlogDatabase
import dev.similin.finlog.utils.receipt.PendingReceiptHolder
import org.koin.dsl.module

/** Provides preferences, the database and the repositories built on top of it **/
val dataModule = module {

    single { AppPreference(settings = get()) }

    single { FinlogDatabase(driver = get()) }

    single { CategoryRepository(database = get()) }

    single { PaymentMethodRepository(database = get()) }

    single { ExpenseRepository(database = get()) }

    single { PendingReceiptHolder() }
}
