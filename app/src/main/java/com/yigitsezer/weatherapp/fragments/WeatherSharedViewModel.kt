package com.yigitsezer.weatherapp.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yigitsezer.weatherapp.data.domain.model.Weather
import com.yigitsezer.weatherapp.data.repository.WeatherRepository
import com.yigitsezer.weatherapp.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSharedViewModel @Inject constructor(
    private var weatherRepo: WeatherRepository
): ViewModel() {
    val weather = SingleLiveEvent<Weather>()

    //In order to block multiple network requests at once
    private var isAvailable = true

    fun getForecast(woeid: Int)  {
        if (isAvailable) {
            isAvailable = false
            var a: Weather? = null
            viewModelScope.launch(Dispatchers.IO) {
                a = weatherRepo.get(woeid)
            }.invokeOnCompletion {
                a?.let {
                    weather.postValue(it)
                    isAvailable = true
                }
            }
        }
    }
}