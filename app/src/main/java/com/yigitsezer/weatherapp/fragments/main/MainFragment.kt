package com.yigitsezer.weatherapp.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.gson.GsonBuilder
import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.databinding.FragmentMainBinding
import com.yigitsezer.weatherapp.fragments.WeatherSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.yigitsezer.weatherapp.R


@AndroidEntryPoint
class MainFragment: Fragment(), EasyPermissions.PermissionCallbacks {
    private val REQUEST_CHECK_SETTINGS = 1

    private lateinit var binding: FragmentMainBinding
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var service: WeatherApiService? = null

    private val locationListViewModel: LocationListViewModel by viewModels()
    private val weatherSharedViewModel: WeatherSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        service = Retrofit.Builder()
            .baseUrl("https://www.metaweather.com/api/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WeatherApiService::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        if (service == null) {
            service = Retrofit.Builder()
                .baseUrl("https://www.metaweather.com/api/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
                .create(WeatherApiService::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Initial recyclerview list is empty
        binding.locationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.locationsRecyclerView.adapter = LocationListAdapter(listOf()) {
            //navigation, initially leave this blank
        }

        weatherSharedViewModel.weather.observe(viewLifecycleOwner, {
            Log.d("HELLOW", "New forecast is fetched: ${it?.title ?: "null"}")
            if (it != null)
                findNavController().navigate(R.id.action_mainFragment_to_locationInfoFragment)
        })

        locationListViewModel.locations.observe(viewLifecycleOwner, { list : List<Location> ->
            //Update adapter on locations list changed
            binding.locationsRecyclerView.adapter = LocationListAdapter(list) { location : Location ->
                //onclick
                //you can either navigate because livedata is changed or just navigate here
                //because you know data is going to change here anyway
                weatherSharedViewModel.getForecast(location.woeid)

            }
        })

        binding.button.setOnClickListener {
            if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions()
            } else {
                updateLocations()
            }
        }
    }

    //Permissions are already checked where this function is called
    @SuppressLint("MissingPermission")
    private fun updateLocations() {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        }
        fusedLocationClient?.let {
            it.lastLocation.addOnSuccessListener { location : android.location.Location? ->
                //fusedLocationClient.lastLocation may return null in some cases
                //https://developer.android.com/training/location/retrieve-current.html#last-known
                if (location == null) {
                    Log.d("HELLOW", "Creating new location request")
                    val req = LocationRequest.create()
                    req.numUpdates = 1
                    val builder = LocationSettingsRequest.Builder().addLocationRequest(req)
                    val client = LocationServices.getSettingsClient(requireContext())
                    val task = client.checkLocationSettings(builder.build())

                    task.addOnSuccessListener {
                        Log.d("HELLOW", "satisfied")
                        Log.d("HELLOW", "${it.locationSettingsStates.isLocationUsable}")
                        fusedLocationClient?.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)?.addOnSuccessListener {
                                currentLocation : android.location.Location? ->
                            Log.d("HELLOW", "New location request done")
                            currentLocation?.let {
                                locationListViewModel.updateLocations(currentLocation.latitude, currentLocation.longitude)
                            }
                        }
                    }
                    task.addOnFailureListener {
                        Log.d("HELLOW", "not satisfied")
                        if (it is ResolvableApiException){
                            try {
                                startIntentSenderForResult(it.resolution.intentSender,
                                    REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null)
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }
                        }
                    }
                } else {
                    Log.d("HELLOW", "No need for new location client")
                    location.let {
                        locationListViewModel.updateLocations(it.latitude, it.longitude)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (perms.containsAll(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            Log.d("HELLOW", "Granted")
            updateLocations()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        //requestPermissions(
        //    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        //        Manifest.permission.ACCESS_COARSE_LOCATION), 0)

        EasyPermissions.requestPermissions(this, "Location access is denied.", 0,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d("HELLOW", "User agreed to make required location settings changes.")
                    updateLocations()
                }
                Activity.RESULT_CANCELED -> Log.d(
                    "HELLOW",
                    "User chose not to make required location settings changes."
                )
            }
        }
    }
}