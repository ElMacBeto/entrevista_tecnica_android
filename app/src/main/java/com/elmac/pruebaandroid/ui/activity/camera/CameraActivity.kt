package com.elmac.pruebaandroid.ui.activity.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.elmac.pruebaandroid.R
import com.elmac.pruebaandroid.databinding.ActivityCameraBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

const val PHOTO_NAME = "web_photo"
const val PHOTO_PATH_KEY = "web_photo_key"
class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var photoPath = ""
    private lateinit var sharedPref: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=  ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )

        setInitImage()
        setListeners()
    }

    private fun setInitImage() {
        val currentOrientation = resources.configuration.orientation

        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v("TAG", "PORTRAIT !!!")
            val displayMetrics = resources.displayMetrics
            val myHeight = displayMetrics.heightPixels/1.75
            binding.imageView.layoutParams.height = myHeight.toInt()
        }

        photoPath = sharedPref.getString(PHOTO_PATH_KEY, "").toString()
        if (photoPath.isNotEmpty()){
            val bitmap: Bitmap = BitmapFactory.decodeFile(photoPath)
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setListeners(){
        binding.takePhotoBtn.setOnClickListener{
            confirmTakePhoto()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun confirmTakePhoto(){
        val image = File(photoPath)
        if(image.exists()){
            val builder = MaterialAlertDialogBuilder(this, R.style.MyMaterialAlertDialog)
            builder.setMessage("Ya fue seleccionada una foto, desea reemplazarla?")
            builder.setCancelable(true)
            builder.setPositiveButton("si"){ dialog, _ ->
                dispatchTakePictureIntent()
                dialog.dismiss()
            }
            builder.setNegativeButton("no"){dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }else{
            dispatchTakePictureIntent()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            this.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        val fileExist = File(photoPath)
                        if(!fileExist.exists()){
                            createImageFile()
                        }else fileExist

                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri? = applicationContext.let { it1 ->
                            FileProvider.getUriForFile(
                                it1,
                                "com.elmac.pruebaandroid.fileprovider",
                                it
                            )
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    }
                }
            }
        }
        resultLauncherTakePhotoIntent.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val picture = File.createTempFile(
            "JPEG_${PHOTO_NAME}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            photoPath = absolutePath
        }
        return picture
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private var resultLauncherTakePhotoIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val bitmap: Bitmap = BitmapFactory.decodeFile(photoPath)
            val matrix = Matrix()
            matrix.postRotate(90f)
            val rotated = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            binding.imageView.setImageBitmap(rotated)

            // save image rotated
            val file = createImageFile()
            val bos = ByteArrayOutputStream()
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)
            val bArray = bos.toByteArray()
            file.writeBytes(bArray)

            with(sharedPref.edit()) {
                putString(PHOTO_PATH_KEY, photoPath )
                apply()
            }

        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_left)
    }
}