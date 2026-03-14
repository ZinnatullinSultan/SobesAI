package com.example.sobesai.di

import com.example.sobesai.data.local.provideDataStore
import com.example.sobesai.data.remote.createHttpClient
import com.example.sobesai.data.repository.LoginRepository
import com.example.sobesai.data.repository.SettingsRepositoryImpl
import com.example.sobesai.data.repository.SpecializationsRepository
import com.example.sobesai.domain.repository.SettingsRepository
import com.example.sobesai.presentation.MainViewModel
import com.example.sobesai.presentation.Specialization.SpecializationViewModel
import com.example.sobesai.presentation.login.LoginViewModel
import com.example.sobesai.presentation.main.MainScreenViewModel
import com.example.sobesai.presentation.welcome.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { provideDataStore() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single { createHttpClient(get()) }
    single { LoginRepository() }

    single { SpecializationsRepository(get()) }

    viewModelOf(::MainViewModel)
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::WelcomeViewModel)

    viewModel { (id: Long) -> SpecializationViewModel(repository = get(), id = id) }
}
