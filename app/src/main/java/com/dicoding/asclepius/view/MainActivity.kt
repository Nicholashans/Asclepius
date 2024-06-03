package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.lang.StringBuilder
import java.text.NumberFormat
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigation: BottomNavigationView

    private var currentImageUri: Uri? = null

    private var GALLERY_REQ_CODE = 1000

    private lateinit var sourceUri: String
    private lateinit var destinationUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        binding.progressIndicator.visibility = View.GONE
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val sortedCategories = it[0]
                                    .categories.sortedByDescending { it?.score }
                                val displayResult = sortedCategories.joinToString("\n") {
                                    "${it.label} " + NumberFormat.getPercentInstance()
                                        .format(it.score).trim()
                                }
                                var resultText = displayResult
                                moveToResult(resultText)
                            } else {
                                showToast("Select the image")
                            }
                        }
                    }
                }
            }
        )

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage()
            } ?: showToast("Please select an image first")
        }

        navigation = findViewById(R.id.navBar)
        navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_home -> {
                    finish()
                    intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_news -> {
                    intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_history -> {
                    intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        val intentGallery = Intent(Intent.ACTION_PICK)
        intentGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intentGallery, GALLERY_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                if (data != null) {
                    currentImageUri = data.data
                    currentImageUri?.let { startCrop(it) }
                    showImage()
                } else {
                    showToast("Failed to get image URI")
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                val croppedUri = UCrop.getOutput(data!!)
                if (croppedUri != null) {
                    showUCrop(croppedUri)
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                showToast("Failed to Crop")
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let { imageUri ->
            binding.previewImageView.setImageURI(imageUri)
        } ?: Log.d(TAG, "There is no image, try again!")
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        currentImageUri?.let { imageUri ->
            binding.progressIndicator.visibility = View.VISIBLE
            imageClassifierHelper.classifyStaticImage(imageUri)
        }
    }

    private fun moveToResult(resultClassification: String) {
        val intent = Intent(this, ResultActivity::class.java)
        currentImageUri?.let { imageUri ->
            intent.putExtra(ResultActivity.IMAGE_RESULT, imageUri.toString())
            intent.putExtra(ResultActivity.EXTRA_RESULT, resultClassification)
            startActivityForResult(intent, 3000 )
        }
    }

    val options = UCrop.Options()

    private fun startCrop(imageUri: Uri) {
        destinationUri = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
        UCrop.of(imageUri, Uri.fromFile(File(cacheDir, destinationUri)))
            .withOptions(options)
            .withMaxResultSize(1080, 1080)
            .start(this)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUCrop(imageUri: Uri) {
        binding.previewImageView.setImageURI(imageUri)
        currentImageUri = imageUri
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}