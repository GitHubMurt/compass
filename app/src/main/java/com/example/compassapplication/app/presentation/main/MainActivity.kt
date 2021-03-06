package com.example.compassapplication.app.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.compassapplication.R
import com.example.compassapplication.app.presentation.utils.PermissionUtil
import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModelFactory: MainViewModelFactory by inject()
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        requestPermission()
        listenLocationChanges()
    }

    private fun requestPermission() {
        if (PermissionUtil.isLocationPermissionGranted(this)) {
            Timber.d("Permissions Granted")
            viewModel.isLocationPermissionGranted.value = true
        } else {
            viewModel.isLocationPermissionGranted.value = false
            PermissionUtil.justifyAskingForLocationPermission(
                this
            ) { permissions ->
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_CODE_LOCATION
                );
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> requestPermission()
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun listenLocationChanges() {
        val locationUnlockObserver = object : Observer<DomainLocation> {
            override fun onChanged(t: DomainLocation?) {
                if (t != null) {
                    Toast.makeText(
                        this@MainActivity,
                        "Custom destination Unlocked!",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.currentLocation.removeObserver(this)
                }
            }
        }
        viewModel.currentLocation.observe(this, locationUnlockObserver)
    }

    companion object {
        private const val REQUEST_CODE_LOCATION = 123
    }
}
