package com.example.timecalculator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.timecalculator.utils.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class TimeCalculatorApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun provideSharedPreferences(
    @ApplicationContext context: Context
  ): SharedPreferences {
    return context.getSharedPreferences("time_calculator_prefs", Context.MODE_PRIVATE)
  }

  @Provides
  @Singleton
  fun provideAppDatabase(
    @ApplicationContext context: Context
  ): AppDatabase {
    return Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      AppDatabase.DATABASE_NAME
    ).build()
  }
}