package com.example.sobesai.di

import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.data.remote.createHttpClient
import com.example.sobesai.data.repository.LoginRepositoryImpl
import com.example.sobesai.data.repository.SettingsRepositoryImpl
import com.example.sobesai.data.repository.SpecializationsRepositoryImpl
import com.example.sobesai.domain.repository.LoginRepository
import com.example.sobesai.domain.repository.SettingsRepository
import com.example.sobesai.domain.repository.SpecializationsRepository
import com.example.sobesai.domain.usecase.auth.GetProfileUseCase
import com.example.sobesai.domain.usecase.auth.LoginUseCase
import com.example.sobesai.domain.usecase.auth.LogoutUseCase
import com.example.sobesai.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.example.sobesai.domain.usecase.onboarding.GetInitialAppStateUseCase
import com.example.sobesai.domain.usecase.specialization.GetSpecializationUseCase
import com.example.sobesai.domain.usecase.specialization.GetSpecializationsUseCase
import com.example.sobesai.domain.usecase.specialization.SortSpecializationsUseCase
import com.example.sobesai.domain.usecase.specialization.TogglePinUseCase
import com.example.sobesai.presentation.MainViewModel
import com.example.sobesai.presentation.login.LoginViewModel
import com.example.sobesai.presentation.main.MainScreenViewModel
import com.example.sobesai.presentation.profile.ProfileViewModel
import com.example.sobesai.presentation.specialization.SpecializationViewModel
import com.example.sobesai.presentation.welcome.WelcomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect fun platformModule(): Module

val appModule = module {
    includes(platformModule())

    single<SettingsRepository> { SettingsRepositoryImpl(get(), get()) }
    single<LoginRepository> { LoginRepositoryImpl() }
    single<SpecializationsRepository> {
        SpecializationsRepositoryImpl(
            get(),
            get<LocalDataSource>()
        )
    }

    single { createHttpClient(get()) }

    factoryOf(::GetProfileUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::GetInitialAppStateUseCase)
    factoryOf(::GetSpecializationUseCase)
    factoryOf(::GetSpecializationsUseCase)
    factoryOf(::TogglePinUseCase)
    factoryOf(::SortSpecializationsUseCase)

    viewModelOf(::MainViewModel)
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::WelcomeViewModel)

    viewModel { (id: Long) ->
        SpecializationViewModel(
            getSpecializationUseCase = get(),
            id = id
        )
    }
}
