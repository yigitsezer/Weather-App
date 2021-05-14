package com.yigitsezer.weatherapp.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yigitsezer.weatherapp.data.domain.model.Weather
import com.yigitsezer.weatherapp.data.repository.WeatherRepository
import com.yigitsezer.weatherapp.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class WeatherSharedViewModel @Inject constructor(
    private var weatherRepo: WeatherRepository
): ViewModel() {
    //private val _weather: MutableLiveData<Weather> = MutableLiveData(null)
    val weather = SingleLiveEvent<Weather>()

    fun getForecast(woeid: Int)  {
        Log.d("HELLOW", "making info request with code: $woeid")
        var a: Weather? = null
        viewModelScope.launch(Dispatchers.IO) {
            a = weatherRepo.get(woeid)
        }.invokeOnCompletion {
            Log.d("HELLOW", "UPDATING WEATHER FOR: ${a?.title ?: "null"}")
            a?.let { weather.postValue(it) }
        }
    }
}