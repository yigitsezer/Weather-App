package com.yigitsezer.weatherapp.fragments.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.data.repository.LocationRepository
import com.yigitsezer.weatherapp.di.RepositoryModule
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor (
    private var service: WeatherApiService,
    private var repository: LocationRepository
    ): ViewModel() {

    private val _locations: MutableLiveData<List<Location>> = MutableLiveData()
    val locations: LiveData<List<Location>> = _locations


}