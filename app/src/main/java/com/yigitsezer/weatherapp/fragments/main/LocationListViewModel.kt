package com.yigitsezer.weatherapp.fragments.main

import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.yigitsezer.weatherapp.WeatherApplication
import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.domain.model.Weather
import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.data.repository.LocationRepository
import com.yigitsezer.weatherapp.data.repository.LocationRepositoryImplementation
import com.yigitsezer.weatherapp.data.repository.WeatherRepository
import com.yigitsezer.weatherapp.di.RepositoryModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor (
    private var locationRepo: LocationRepository,
    ): ViewModel() {

    private val _locations: MutableLiveData<List<Location>> = MutableLiveData(listOf())
    val locations: LiveData<List<Location>> get() = _locations

    private var latitute: Double? = null
    private var longitude: Double? = null

    fun setLattLong(latitute: Double, longitude: Double) {
        this.latitute = latitute
        this.longitude = longitude
    }

    fun updateLocations(lat: Double, lon: Double) {
        Log.d("HELLOW", "$lat, $lon")
        viewModelScope.launch {
            withContext(IO) {
                //_locations.value = locationRepo.search(lat, lon)
                _locations.postValue(locationRepo.search(lat, lon))
            }
        }
    }
}