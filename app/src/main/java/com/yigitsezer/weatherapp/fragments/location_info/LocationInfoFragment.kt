package com.yigitsezer.weatherapp.fragments.location_info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yigitsezer.weatherapp.databinding.FragmentLocationInfoBinding
import com.yigitsezer.weatherapp.fragments.WeatherSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class LocationInfoFragment: Fragment() {
    private lateinit var binding: FragmentLocationInfoBinding

    private val weatherSharedViewModel: WeatherSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forecastList.layoutManager = LinearLayoutManager(requireContext())
        Log.d("HELLOW", "I GOT NEW WEATHER: ${weatherSharedViewModel.weather.value}")

        binding.locationName.text = weatherSharedViewModel.weather.value?.title

        var forecastList = weatherSharedViewModel.weather.value?.consolidatedWeather

        var dayNames = ArrayList<String>()
        val sdf = SimpleDateFormat("EEEE dd-MMM-yyyy")
        for (i in 0..6) {
            val calendar: Calendar = GregorianCalendar()
            calendar.add(Calendar.DATE, i)
            val day: String = sdf.format(calendar.time)
            dayNames.add(day.split(" ")[0])
        }
        dayNames[0] = "Today"
        forecastList?.let {
            binding.forecastList.adapter = ForecastListAdapter(it, dayNames)
        }
        //for (i in forecastList!!)
        //    Log.d("HELLOW", "forecast:" + i.applicableDate.toString())
    }
}