package com.example.sobesai.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

actual class PlatformContext(val context: Context)

actual fun getDatabaseBuilder(context: PlatformContext): RoomDatabase.Builder<AppDatabase> {
    val dbFile = context.context.getDatabasePath(AppDatabase.DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = context.context.applicationContext,
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}
