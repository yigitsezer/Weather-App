package com.yigitsezer.weatherapp.di

import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.data.repository.LocationRepository
import com.yigitsezer.weatherapp.data.repository.LocationRepositoryImplementation
import com.yigitsezer.weatherapp.data.repository.WeatherRepository
import com.yigitsezer.weatherapp.data.repository.WeatherRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLocationRepository(service: WeatherApiService): LocationRepository {
        return LocationRepositoryImplementation(service)
    }

    @Singleton
    @Provides
    fun provideWeatherRepository(service: WeatherApiService): WeatherRepository {
        return WeatherRepositoryImplementation(service)
    }
}