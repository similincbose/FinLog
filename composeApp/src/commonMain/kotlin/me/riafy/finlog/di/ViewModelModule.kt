package me.riafy.finlog.di

import me.riafy.finlog.ui.addexpense.AddExpenseViewModel
import me.riafy.finlog.ui.expensedetail.ExpenseDetailViewModel
import me.riafy.finlog.ui.home.HomeViewModel
import me.riafy.finlog.ui.insights.InsightsViewModel
import me.riafy.finlog.ui.settings.SettingsViewModel
import me.riafy.finlog.ui.transactions.TransactionsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::InsightsViewModel)
    viewModelOf(::SettingsViewModel)

    // Take a runtime id (an existing expense to load, or null for a fresh one),
    // so viewModelOf's constructor-reflection can't wire them automatically.
    viewModel { params -> AddExpenseViewModel(params.getOrNull(), get(), get(), get(), get()) }
    viewModel { params -> ExpenseDetailViewModel(params.get(), get()) }
}
