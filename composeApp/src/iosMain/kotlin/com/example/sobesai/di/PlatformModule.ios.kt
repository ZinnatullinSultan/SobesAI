package com.example.sobesai.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.sobesai.data.local.AppDatabase
import com.example.sobesai.data.local.DataStoreContext
import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.data.local.LocalDataSourceImpl
import com.example.sobesai.data.local.PlatformContext
import com.example.sobesai.data.local.SecureTokenStorage
import com.example.sobesai.data.local.getDatabaseBuilder
import com.example.sobesai.data.local.provideDataStore
import com.liftric.kvault.KVault
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<DataStore<Preferences>> { provideDataStore(DataStoreContext()) }
    
    single { KVault("secure_tokens") }
    single { SecureTokenStorage(get()) }

    single<AppDatabase> { getDatabaseBuilder(PlatformContext()).build() }

    single { get<AppDatabase>().specializationDao() }

    single<LocalDataSource> { LocalDataSourceImpl(get()) }
}
