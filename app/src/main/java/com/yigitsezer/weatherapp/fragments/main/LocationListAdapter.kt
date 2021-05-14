package com.yigitsezer.weatherapp.fragments.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yigitsezer.weatherapp.R
import com.yigitsezer.weatherapp.data.domain.model.Location

class LocationListAdapter(private var locations: List<Location>, val onItemClicked: (Location) -> Unit):
    RecyclerView.Adapter<LocationListAdapter.LocationViewHolder>() {
    class LocationViewHolder(view: View, onItemClicked: (Int) -> Unit): RecyclerView.ViewHolder(view) {
        val locationName: TextView = view.findViewById(R.id.location_name)

        init {
            view.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LocationViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_view, viewGroup, false)

        return LocationViewHolder(view) {
            onItemClicked(locations[it])
        }
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.locationName.text = locations[position].name
    }

    override fun getItemCount(): Int {
        return locations.size
    }

}