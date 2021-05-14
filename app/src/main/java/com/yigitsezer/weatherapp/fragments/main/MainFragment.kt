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
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.yigitsezer.weatherapp.R
import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.databinding.FragmentMainBinding
import com.yigitsezer.weatherapp.fragments.WeatherSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class MainFragment: Fragment(), EasyPermissions.PermissionCallbacks {
    private val REQUEST_CHECK_SETTINGS = 1

    private lateinit var binding: FragmentMainBinding
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val locationListViewModel: LocationListViewModel by viewModels()
    private val weatherSharedViewModel: WeatherSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Initial recyclerview list is empty
        binding.locationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.locationsRecyclerView.adapter = LocationListAdapter(listOf()) {
            //navigation, initially leave this blank
        }
        binding.locationsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                LinearLayout.VERTICAL
            ))

        locationListViewModel.locations.observe(viewLifecycleOwner, { list : List<Location> ->
            //Update adapter on locations list changed
            weatherSharedViewModel.weather.observe(viewLifecycleOwner, {
                if (it != null) {
                    findNavController().navigate(R.id.locationInfoFragment)
                }
            })

            binding.locationsRecyclerView.adapter = LocationListAdapter(list) { location : Location ->
                //onclick
                //It turns out navigating on data change is bad and making an observer on
                //each recycler view item is also bad. I might look into this later.
                weatherSharedViewModel.getForecast(location.woeid)
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {

            if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions()
            } else {
                updateLocations()
            }
            //This should actually be done asynchronously based on the actions above
            binding.swipeToRefreshText.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
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
                    val req = LocationRequest.create()
                    req.numUpdates = 1
                    val builder = LocationSettingsRequest.Builder().addLocationRequest(req)
                    val client = LocationServices.getSettingsClient(requireContext())
                    val task = client.checkLocationSettings(builder.build())

                    task.addOnSuccessListener {
                        fusedLocationClient?.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)?.addOnSuccessListener {
                                currentLocation : android.location.Location? ->
                            currentLocation?.let {
                                locationListViewModel.updateLocations(currentLocation.latitude, currentLocation.longitude)
                            }
                        }
                    }
                    task.addOnFailureListener {
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
                    updateLocations()
                }
                Activity.RESULT_CANCELED -> Log.d(
                    "Debug",
                    "User chose not to make required location settings changes."
                )
            }
        }
    }
}