package com.yigitsezer.weatherapp.fragments.main

import android.util.Log
import androidx.lifecycle.*
import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor (
    private var locationRepo: LocationRepository,
): ViewModel() {

    private val _locations: MutableLiveData<List<Location>> = MutableLiveData(listOf())
    val locations: LiveData<List<Location>> get() = _locations

    fun updateLocations(lat: Double, lon: Double) {
        var newLocations: List<Location>? = null
        viewModelScope.launch {
            withContext(IO) {
                //_locations.value = locationRepo.search(lat, lon)
                try {
                    newLocations = locationRepo.search(lat, lon)
                    _locations.postValue(newLocations)
                } catch (e: Exception) {
                    //Handle UnknownHostException
                }
            }
        }
    }
}