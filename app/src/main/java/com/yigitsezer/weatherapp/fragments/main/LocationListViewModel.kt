package com.yigitsezer.weatherapp.fragments.main

import androidx.lifecycle.*
import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun updateLocations(lat: Double, lon: Double) {
        viewModelScope.launch {
            withContext(IO) {
                //_locations.value = locationRepo.search(lat, lon)
                _locations.postValue(locationRepo.search(lat, lon))
            }
        }
    }
}