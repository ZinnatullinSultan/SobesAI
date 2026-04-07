package com.example.sobesai.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.sobesai.data.local.database.AppDatabase
import com.example.sobesai.data.local.database.PlatformContext
import com.example.sobesai.data.local.database.getDatabaseBuilder
import com.example.sobesai.data.local.datasource.LocalDataSource
import com.example.sobesai.data.local.datasource.LocalDataSourceImpl
import com.example.sobesai.data.local.storage.DataStoreContext
import com.example.sobesai.data.local.storage.SecureTokenStorage
import com.example.sobesai.data.local.storage.provideDataStore
import com.liftric.kvault.KVault
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<DataStore<Preferences>> { provideDataStore(DataStoreContext(androidContext())) }

    single { KVault(androidContext(), "secure_tokens") }
    singleOf(::SecureTokenStorage)

    single<AppDatabase> { getDatabaseBuilder(PlatformContext(androidContext())).build() }
    single { get<AppDatabase>().specializationDao() }

    singleOf(::LocalDataSourceImpl) { bind<LocalDataSource>() }
}
