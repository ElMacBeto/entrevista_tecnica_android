package com.elmac.pruebaandroid

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.elmac.pruebaandroid.databinding.ActivityMainBinding
import com.elmac.pruebaandroid.ui.activity.camera.CameraActivity
import com.elmac.pruebaandroid.ui.activity.location.LocationActivity

const val LOCATION_REQUEST_CODE = 1
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setListeners()
    }

    private fun setListeners(){
        with(binding){
            locationBtn.setOnClickListener{
                if(setPermission()){
                    val intent = Intent(applicationContext, LocationActivity::class.java)
                    changeActivity(intent)
                }
            }
            photoBtn.setOnClickListener{
                val intent = Intent(applicationContext, CameraActivity::class.java)
                changeActivity(intent)
            }
        }
    }

    private fun changeActivity(intent: Intent){
        startActivity(intent)
        overridePendingTransition(R.anim.enter_from_rigth, R.anim.exit_from_right)
    }

    private fun setPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==LOCATION_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(applicationContext, LocationActivity::class.java)
                changeActivity(intent)
            }else
                Toast.makeText(this, "La aplicación requiere permiso para funcionar", Toast.LENGTH_SHORT).show()

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


}