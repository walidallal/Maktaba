package com.ElOuedUniv.maktaba.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ElOuedUniv.maktaba.data.local.dao.BookDao
import com.ElOuedUniv.maktaba.data.local.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MaktabaDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        const val DATABASE_NAME = "maktaba.db"
    }
}
