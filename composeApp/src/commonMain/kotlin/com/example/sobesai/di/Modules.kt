package com.example.sobesai.di

import com.example.sobesai.data.local.AppDatabase
import com.example.sobesai.data.local.OnboardingStorage
import com.example.sobesai.data.local.ProfileStorage
import com.example.sobesai.data.remote.api.AuthApi
import com.example.sobesai.data.remote.api.InterviewApi
import com.example.sobesai.data.remote.api.SpecializationsApi
import com.example.sobesai.data.remote.clients.createGeminiClient
import com.example.sobesai.data.remote.clients.createHttpClient
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
import com.example.sobesai.domain.usecase.auth.RegisterUseCase
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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val QUALIFIER_SUPABASE = "supabase"
private const val QUALIFIER_GEMINI = "gemini"

expect fun platformModule(): Module

val appModule = module {
    includes(platformModule())

    single { get<AppDatabase>().interviewDao() }

    singleOf(::OnboardingStorage)
    singleOf(::ProfileStorage)

    single(named(QUALIFIER_SUPABASE)) { createHttpClient(get()) }
    single(named(QUALIFIER_GEMINI)) { createGeminiClient() }

    single { AuthApi(get(named(QUALIFIER_SUPABASE))) }
    single { SpecializationsApi(get(named(QUALIFIER_SUPABASE))) }
    single { InterviewApi(get(named(QUALIFIER_GEMINI))) }

    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
    singleOf(::LoginRepositoryImpl) { bind<LoginRepository>() }
    singleOf(::SpecializationsRepositoryImpl) { bind<SpecializationsRepository>() }
    singleOf(::InterviewRepositoryImpl) { bind<InterviewRepository>() }

    factoryOf(::GetProfileUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::GetInitialAppStateUseCase)
    factoryOf(::GetSpecializationUseCase)
    factoryOf(::GetSpecializationsUseCase)
    factoryOf(::TogglePinUseCase)
    factoryOf(::SortSpecializationsUseCase)
    factoryOf(::StartInterviewUseCase)
    factoryOf(::SendChatMessageUseCase)

    factoryOf(::InterviewPromptProvider)

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
