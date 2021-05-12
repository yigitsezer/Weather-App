package com.yigitsezer.weatherapp.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.data.network.model.LocationNetworkEntity
import com.yigitsezer.weatherapp.data.repository.LocationRepositoryImplementation
import com.yigitsezer.weatherapp.data.repository.WeatherRepositoryImplementation
import com.yigitsezer.weatherapp.data.util.LocationEntityMapper
import com.yigitsezer.weatherapp.databinding.FragmentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainFragment: Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentMainBinding
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var service: WeatherApiService? = null

    private var locations: List<Location>? = null //nearby locations

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
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        }
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

        binding.button.setOnClickListener {
            //fusedLocationClient.lastLocation @RequiresPermission, instead of ignoring the annotation
            //permissions can be checked the old fashioned way
            /*
            ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

             */
            if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d("HELLOW", "nay")
                requestPermissions()
            } else {
                Log.d("HELLOW", "aye")
                getNearbyLocations()
            }

            //requestPermissions()
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
            getNearbyLocations()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            Log.d("HELLOW", "some permissions are not denied")
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        Log.d("HELLOW", "nay1")
        EasyPermissions.requestPermissions(this, "Location access is denied.", 0,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        Log.d("HELLOW", "nay2")
    }


    private fun getNearbyLocations() {
        //Block which will call this function already handles this exception but whatever
        try {
            /*
            android.location.Location gives current location whereas the implementation of Location
            in this package contains a certain point of interest such as a city.
             */
            fusedLocationClient!!.lastLocation.addOnSuccessListener { location : android.location.Location ->
                Log.d("HELLOW", "${location.latitude}, " +
                        "${location.longitude}, " +
                        "${location.altitude}")

                val latitude = location.latitude
                val longitude = location.longitude

                val locationRepo = LocationRepositoryImplementation(service!!)

                CoroutineScope(IO).launch {
                    val locations = locationRepo.search(latitude, longitude)
                    withContext(Main) {
                        binding.locationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        binding.locationsRecyclerView.adapter = LocationListAdapter(locations) {
                            //navigation
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            //SecurityException
        }
    }
}