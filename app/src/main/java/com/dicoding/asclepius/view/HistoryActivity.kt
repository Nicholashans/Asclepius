package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.HistoryAdapter
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : AppCompatActivity() {


    private var _activityHistoryBinding: ActivityHistoryBinding? = null
    private val binding get() = _activityHistoryBinding

    private lateinit var adapter: HistoryAdapter

    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityHistoryBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        navigation = findViewById(R.id.navBar3)

        navigation.selectedItemId = R.id.navigation_history

        navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_home -> {
                    intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_news -> {
                    finish()
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

        val historyViewModel = obtainViewModel(this@HistoryActivity)
        adapter = HistoryAdapter()

        historyViewModel.getAllHistory().observe(this) {historyList ->
            if (historyList != null) {
                adapter.setListHistory(historyList)
            }
        }

        binding?.rvHistory?.layoutManager = LinearLayoutManager(this)
        binding?.rvHistory?.setHasFixedSize(true)
        binding?.rvHistory?.adapter = adapter

    }

    override fun onDestroy() {
        super.onDestroy()
        _activityHistoryBinding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity) : HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(HistoryViewModel::class.java)
    }
}