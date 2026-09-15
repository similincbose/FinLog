package me.riafy.finlog.di

import me.riafy.finlog.data.local.preference.AppPreference
import me.riafy.finlog.data.repo.CategoryRepository
import me.riafy.finlog.data.repo.ExpenseRepository
import me.riafy.finlog.data.repo.PaymentMethodRepository
import me.riafy.finlog.database.FinlogDatabase
import org.koin.dsl.module

/** Provides preferences, the database and the repositories built on top of it **/
val dataModule = module {

    single { AppPreference(settings = get()) }

    single { FinlogDatabase(driver = get()) }

    single { CategoryRepository(database = get()) }

    single { PaymentMethodRepository(database = get()) }

    single { ExpenseRepository(database = get()) }
}
