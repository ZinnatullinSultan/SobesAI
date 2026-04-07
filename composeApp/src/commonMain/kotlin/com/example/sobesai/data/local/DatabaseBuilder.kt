package com.example.sobesai.data.local

import androidx.room.RoomDatabase

expect class PlatformContext

expect fun getDatabaseBuilder(context: PlatformContext): RoomDatabase.Builder<AppDatabase>
