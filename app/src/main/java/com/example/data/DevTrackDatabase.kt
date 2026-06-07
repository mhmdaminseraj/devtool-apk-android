package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Project::class, Page::class, Section::class, Task::class],
    version = 1,
    exportSchema = false
)
abstract class DevTrackDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun pageDao(): PageDao
    abstract fun sectionDao(): SectionDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: DevTrackDatabase? = null

        fun getDatabase(context: Context): DevTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DevTrackDatabase::class.java,
                    "devtrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
