package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.view.insert.HistoryAddViewModel

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private var history: History? = null
    private lateinit var historyAddViewModel: HistoryAddViewModel
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyViewModel = obtainHistoryViewModel(this@ResultActivity)
        historyAddViewModel = obtainHistoryAddViewModel(this@ResultActivity)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.

        val result = intent.getStringExtra(EXTRA_RESULT)
        val imageUri = intent.getStringExtra(IMAGE_RESULT)

        val dominantPrediction = result?.split("\n")?.firstOrNull()

        val text = "Confidence Score : \n$result"
        val prediction = "Prediction : \n$dominantPrediction\n\n"

        binding.resultText.text = prediction + text

        val image = Uri.parse(imageUri)
        showImage(image)

        history = History()
        binding.fabAdd.setOnClickListener {
            history.let { history ->
                history?.prediction = dominantPrediction
                history?.photoString = imageUri
                history?.confidenceScore = text
                historyAddViewModel.insert(history as History)
            }
            showToast("Data Berhasil Disimpan!")
            binding.fabAdd.hide()
            binding.fabAdd.isEnabled = false
        }

    }

    private fun showImage(imageUri: Uri) {
        binding.resultImage.setImageURI(imageUri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        private const val TAG = "ResultActivity"
        const val IMAGE_RESULT = "image_result"
    }

    private fun obtainHistoryAddViewModel(activity: AppCompatActivity) : HistoryAddViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(HistoryAddViewModel::class.java)
    }

    private fun obtainHistoryViewModel(activity: AppCompatActivity) : HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity,factory).get(HistoryViewModel::class.java)
    }


}