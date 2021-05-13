package com.yigitsezer.weatherapp.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yigitsezer.weatherapp.data.domain.model.Weather
import com.yigitsezer.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherSharedViewModel @Inject constructor(
    private var weatherRepo: WeatherRepository
): ViewModel() {
    private val _weather: MutableLiveData<Weather> = MutableLiveData(null)
    val weather: LiveData<Weather> get() = _weather

    fun setWeather(weather: Weather) {
        _weather.postValue(weather)
    }

    fun getForecast(woeid: Int)  {
        Log.d("HELLOW", "making info request with code: $woeid")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var a = weatherRepo.get(woeid)
                //Log.d("HELLOW", "UPDATED WEATHER: ${a?.consolidatedWeather.size ?: "null"}")
                _weather.postValue(a)
            }
        }
    }
}