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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.gson.GsonBuilder
import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.data.repository.LocationRepositoryImplementation
import com.yigitsezer.weatherapp.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment: Fragment(), EasyPermissions.PermissionCallbacks {
    private val REQUEST_CHECK_SETTINGS = 1

    private lateinit var binding: FragmentMainBinding
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var service: WeatherApiService? = null

    private val locationListViewModel: LocationListViewModel by viewModels()


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
            if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d("HELLOW", "nay")
                requestPermissions()
            } else {
                Log.d("HELLOW", "aye")
                getNearbyLocations()
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
            getNearbyLocations()
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

    private fun getNearbyLocations() {
        try {
            /* android.location.Location gives current location whereas the implementation of Location
            in this package contains a certain point of interest such as a city. */
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location : android.location.Location? ->
                //fusedLocationClient.lastLocation may return null in some cases
                //https://developer.android.com/training/location/retrieve-current.html#last-known
                if (location == null) {
                    //Toast.makeText(requireContext(), "Could not retrieve location", LENGTH_SHORT).show()
                    //return@addOnSuccessListener
                    Log.d("HELLOW", "Creating new location request")
                    val req = LocationRequest.create()
                    req.numUpdates = 1
                    var builder = LocationSettingsRequest.Builder().addLocationRequest(req)
                    var client = LocationServices.getSettingsClient(requireContext())
                    var task = client.checkLocationSettings(builder.build())

                    task.addOnSuccessListener {
                        Log.d("HELLOW", "satisfied")
                        Log.d("HELLOW", "${it.locationSettingsStates.isLocationUsable}")
                        fusedLocationClient?.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)?.addOnSuccessListener {
                                currentLocation : android.location.Location? ->
                            Log.d("HELLOW", "New location request done")
                            currentLocation?.let { getNearbyLocations2(currentLocation.longitude, currentLocation.latitude) }
                        }
                    }
                    task.addOnFailureListener {
                        Log.d("HELLOW", "not satisfied")
                        if (it is ResolvableApiException){
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                startIntentSenderForResult(it.resolution.intentSender,
                                    REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null)
                                //Below code is for activities while the above code is for fragments
                                /*
                                it.startResolutionForResult(
                                    requireActivity(),
                                    REQUEST_CHECK_SETTINGS
                                )
                                 */
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }
                        }
                    }
                } else {
                    Log.d("HELLOW", "No need for new location client")
                    location?.let {
                        getNearbyLocations2(location.latitude, location.longitude)
                    }
                }
            }
        } catch (e: SecurityException) {
            //SecurityException
        }
    }

    private fun getNearbyLocations2(lat: Double, lon: Double) {
        Log.d("HELLOW", "$lat, $lon")
        val locationRepo = LocationRepositoryImplementation(service!!)
        CoroutineScope(IO).launch {
            val locations = locationRepo.search(lat, lon)
            withContext(Main) {
                binding.locationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.locationsRecyclerView.adapter = LocationListAdapter(locations) {
                    //navigation
                }
            }
        }
    }

    //Couldnt be bothered to create a logic that access a single endpoint for getNearbyLocations2()
    //as the function's name already suggests. Permissions are checked before REQUEST_CHECK_SETTINGS is ever done.
    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d("HELLOW", "User agreed to make required location settings changes.")

                    fusedLocationClient?.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)?.addOnSuccessListener {
                            currentLocation : android.location.Location? ->
                        Log.d("HELLOW", "New location request done")
                        currentLocation?.let { getNearbyLocations2(currentLocation.longitude, currentLocation.latitude) }
                    }
                }
                Activity.RESULT_CANCELED -> Log.d(
                    "HELLOW",
                    "User chose not to make required location settings changes."
                )
            }
        }
    }
}