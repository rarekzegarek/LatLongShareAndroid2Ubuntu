package com.github.rarekzegarek.latlongshare

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    sendLocation(it.latitude, it.longitude)
                } ?: run {
                    Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendLocation(lat: Double, long: Double) {
        Thread {
            var socket: Socket? = null
            try {
                socket = Socket("192.168.1.23", 10110).apply {
                    soTimeout = 5000 // Timeout 5 sekund
                }
                val output = PrintWriter(socket.getOutputStream(), true)
                val nmeaSentence = String.format("$GPGGA,%1$.5f,N,%2$.5f,E,1,08,0.9,545.4,M,46.9,M,,*", lat, long)
                val checksum = nmeaSentence
                    .substring(nmeaSentence.indexOf('$') + 1, nmeaSentence.indexOf('*'))
                    .fold(0) { acc, char -> acc xor char.code }
                val fullSentence = "$nmeaSentence${checksum.toString(16).uppercase().padStart(2, '0')}"
                output.println(fullSentence)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Sent: $fullSentence", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("MainActivity", "Network error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                socket?.close()
            }
        }.start()
    }
}

