package com.murad.presensi.view.user.home

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.murad.presensi.R
import com.murad.presensi.databinding.ActivityHomeUserBinding
import com.murad.presensi.utils.UtilsTIme
import com.murad.presensi.view.ViewModelFactory
import com.murad.presensi.view.admin.home.HomeAdminActivity
import com.murad.presensi.view.admin.home.HomeAdminViewModel
import com.murad.presensi.view.login.LoginActivity
import com.murad.presensi.view.user.history.HistoryActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class HomeUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeUserBinding
    private val utils = UtilsTIme()
    private val updateInterval = 1000L // 1 detik
    private var job: Job? = null
    private val geoFencingCenter = Location("").apply {
        latitude = -6.354456627353137 // Replace with actual latitude (must be between -90 and 90)
        longitude = 106.8415776664096  // Replace with actual longitude (must be between -180 and 180)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val geoFencingRadius = 50.0 // Radius in meters

    // Initialize ViewModel
    private val viewModel by viewModels<HomeUserViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        getSession()
        observeCheckInStatus()
        observeCheckInMessage()
        setupListeners()
        setupListHistory()
        // Start updating date and time
        startUpdatingDateTime()
        checkPermissionLocation()
    }

    private fun setupListHistory() {
        // Setup RecyclerView and Adapter
        val listAdapter = HomeListAdapter()
        binding.rvHistory.adapter = listAdapter
        binding.rvHistory.layoutManager = LinearLayoutManager(this)

        // Observe the attendance history data
        viewModel.startListeningToAttendanceHistory()
        viewModel.attendanceHistory.observe(this) { attendanceHistoryList ->
            listAdapter.submitAndSortList(attendanceHistoryList) // Submit and sort the list
        }
    }


    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            Log.d("HomeUserActivity", "User: $user")
            if (!user.isLogin) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("HomeUserActivity", "User: ${user.role}")
                if (user.role == "admin") {
                    val intent = Intent(this, HomeAdminActivity::class.java)
                    intent.putExtra("username", user.username)
                    startActivity(intent)
                    finish()
                }
                viewModel.setIsCheckIn(user.isCheckIn)
                binding.tvUsername.text = user.username
            }
        }
    }

    private fun observeCheckInStatus() {
        viewModel.isCheckIn.observe(this) { isCheckIn ->
            Log.d("HomeUserActivity", "Check-in status: $isCheckIn")
            binding.btnMasuk.isEnabled = !isCheckIn
            binding.btnKeluar.isEnabled = isCheckIn
        }
    }

    private fun setupListeners() {
        val date = utils.getDateAndTimeInIndonesia("Asia/Jakarta").first

        binding.btnMasuk.setOnClickListener {
            checkUserLocationAndCheckIn()
        }

        binding.btnKeluar.setOnClickListener {
            viewModel.saveCheckOut(date)
        }

        binding.tvLihatHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("username", binding.tvUsername.text.toString())
            startActivity(intent)
            finish()
        }

        binding.btnLogout.setOnClickListener {
            showPopUp()
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun startUpdatingDateTime() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val (date, time) = utils.getDateAndTimeInIndonesia("Asia/Jakarta")
                binding.tvTannggal.text = date
                binding.tvJamSekarang.text = time
                delay(updateInterval)
            }
        }
    }

    private fun observeCheckInMessage() {
        viewModel.checkIn.observe(this) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
            }
        }
    }


    private fun checkUserLocationAndCheckIn() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val userLocation = Location("").apply {
                            latitude = location.latitude
                            longitude = location.longitude
                        }
                        Log.d("Location", "User Location: $userLocation")
                        val distance = geoFencingCenter.distanceTo(userLocation)

                        if (distance <= geoFencingRadius) {
                            val date = utils.getDateAndTimeInIndonesia("Asia/Jakarta").first
                            viewModel.saveCheckIn(date, location.longitude, location.latitude)
                        } else {
                            showToast("You are out of the allowed area for check-in")
                            Log.d(
                                "HomeUserActivity",
                                "You are out of the allowed area $distance, $userLocation"
                            )
                        }
                    } else {
                        requestLocationUpdates()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Location is not enabled. Please enable location services.",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermission()
        }
    }


    private fun showPopUp() {
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.popup_confirm_logout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                viewModel.logout()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun requestLocationUpdates() {
        // Check if location permissions are granted
        if (checkPermission()) {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // 10 seconds
                fastestInterval = 5000 // 5 seconds
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        // This method is called when location updates are available
                        for (location in locationResult.locations) {
                            // Use the location
                            Log.d(
                                "Location",
                                "Location: ${location.latitude}, ${location.longitude}"
                            )
                            // You can also check distance here and save check-in if within range
                        }
                    }
                },
                Looper.getMainLooper()
            ) // Ensure updates are received on the main thread
        } else {
            // If permissions are not granted, request them
            requestPermission()
        }
    }

    private fun checkPermissionLocation() {
        if (checkPermission()) {
            if (!isLocationEnabled()) {
                Toast.makeText(
                    this,
                    "Location is not enabled, please enable location",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocationPermission && coarseLocationPermission
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            ID_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ID_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                // You can now safely call requestLocationUpdates() here if needed
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                // Optionally, you can show a dialog explaining why the permission is needed
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewModelFactory.clearInstance()
        job?.cancel()
        viewModel.stopListeningToAttendanceHistory()
    }


    companion object {
        const val ID_LOCATION_PERMISSION = 0
    }
}
