package com.example.sobesai.di

import com.example.sobesai.data.local.AppDatabase
import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.data.remote.createGeminiClient
import com.example.sobesai.data.remote.createHttpClient
import com.example.sobesai.data.repository.InterviewRepositoryImpl
import com.example.sobesai.data.repository.LoginRepositoryImpl
import com.example.sobesai.data.repository.SettingsRepositoryImpl
import com.example.sobesai.data.repository.SpecializationsRepositoryImpl
import com.example.sobesai.domain.provider.InterviewPromptProvider
import com.example.sobesai.domain.repository.InterviewRepository
import com.example.sobesai.domain.repository.LoginRepository
import com.example.sobesai.domain.repository.SettingsRepository
import com.example.sobesai.domain.repository.SpecializationsRepository
import com.example.sobesai.domain.usecase.auth.GetProfileUseCase
import com.example.sobesai.domain.usecase.auth.LoginUseCase
import com.example.sobesai.domain.usecase.auth.LogoutUseCase
import com.example.sobesai.domain.usecase.interview.SendChatMessageUseCase
import com.example.sobesai.domain.usecase.interview.StartInterviewUseCase
import com.example.sobesai.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.example.sobesai.domain.usecase.onboarding.GetInitialAppStateUseCase
import com.example.sobesai.domain.usecase.specialization.GetSpecializationUseCase
import com.example.sobesai.domain.usecase.specialization.GetSpecializationsUseCase
import com.example.sobesai.domain.usecase.specialization.SortSpecializationsUseCase
import com.example.sobesai.domain.usecase.specialization.TogglePinUseCase
import com.example.sobesai.presentation.MainViewModel
import com.example.sobesai.presentation.interview.InterviewViewModel
import com.example.sobesai.presentation.login.LoginViewModel
import com.example.sobesai.presentation.main.MainScreenViewModel
import com.example.sobesai.presentation.profile.ProfileViewModel
import com.example.sobesai.presentation.specialization.SpecializationViewModel
import com.example.sobesai.presentation.welcome.WelcomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect fun platformModule(): Module

val appModule = module {
    includes(platformModule())

    single { get<AppDatabase>().interviewDao() }

    single<SettingsRepository> { SettingsRepositoryImpl(get(), get()) }
    single<LoginRepository> { LoginRepositoryImpl() }

    single(named("supabase")) { createHttpClient(get()) }
    single(named("gemini")) { createGeminiClient() }

    single<SpecializationsRepository> {
        SpecializationsRepositoryImpl(
            get(named("supabase")),
            get<LocalDataSource>()
        )
    }

    single<InterviewRepository> {
        InterviewRepositoryImpl(
            client = get(named("gemini")),
            interviewDao = get(),
            promptProvider = get()
        )
    }

    factoryOf(::GetProfileUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::GetInitialAppStateUseCase)
    factoryOf(::GetSpecializationUseCase)
    factoryOf(::GetSpecializationsUseCase)
    factoryOf(::TogglePinUseCase)
    factoryOf(::SortSpecializationsUseCase)
    factoryOf(::StartInterviewUseCase)
    factoryOf(::SendChatMessageUseCase)
    factory { InterviewPromptProvider() }

    viewModelOf(::MainViewModel)
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::WelcomeViewModel)
    viewModelOf(::InterviewViewModel)

    viewModel { (id: Long) ->
        SpecializationViewModel(
            getSpecializationUseCase = get(),
            id = id
        )
    }
}
