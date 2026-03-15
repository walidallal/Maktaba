package com.ElOuedUniv.maktaba.di

import android.content.Context
import androidx.room.Room
import com.ElOuedUniv.maktaba.data.local.MaktabaDatabase
import com.ElOuedUniv.maktaba.data.local.dao.BookDao
import com.ElOuedUniv.maktaba.data.repository.BookRepositoryImpl
import com.ElOuedUniv.maktaba.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MaktabaDatabase =
        Room.databaseBuilder(
            context,
            MaktabaDatabase::class.java,
            MaktabaDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideBookDao(db: MaktabaDatabase): BookDao = db.bookDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        impl: BookRepositoryImpl
    ): BookRepository
}
