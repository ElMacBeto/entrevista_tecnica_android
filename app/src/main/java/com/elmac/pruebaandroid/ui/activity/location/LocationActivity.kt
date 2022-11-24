package com.elmac.pruebaandroid.ui.activity.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.elmac.pruebaandroid.R
import com.elmac.pruebaandroid.databinding.ActivityLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var map: GoogleMap
    private var isMarked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createFragment()
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("SetTextI18n")
    private fun createMarker() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "no cuenta con permisos de locatizacion", Toast.LENGTH_SHORT)
                .show()
            return
        }

        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false

        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener1 = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (isMarked) return
                isMarked=true

                binding.locationTv.text = "Altitud: ${location.latitude} Longitud:${location.longitude}"
                val myLocation = LatLng(location.latitude, location.longitude)
                val marker = MarkerOptions()
                    .position(myLocation)
                    .title("Mi localizacion actual")
                val cameraPosition: CameraPosition = CameraPosition.Builder()
                    .target(myLocation)
                    .zoom(18f)
                    .bearing(90f)
                    .tilt(45f)
                    .build()

                map.addMarker(marker)
                map.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    3000,
                    null
                )
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener1
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_left)
    }

}