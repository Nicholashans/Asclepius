package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.api.response.ArticlesItem
import com.dicoding.asclepius.api.response.NewsResponse
import com.dicoding.asclepius.api.retrofit.ApiConfig
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : AppCompatActivity() {
    private lateinit var navigation: BottomNavigationView
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigation = findViewById(R.id.navBar2)
        navigation.selectedItemId = R.id.navigation_news
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvNews.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvNews.addItemDecoration(itemDecoration)

        findNews()
    }

    private fun findNews() {
        showLoading(true)
        val client = ApiConfig.getApiService().getNews(q, category, language, apiKey)
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setNewsData(responseBody.articles)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setNewsData(news: List<ArticlesItem>) {
        val adapter = NewsAdapter()
        adapter.submitList(news)
        binding.rvNews.adapter = adapter
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarNews.visibility = View.VISIBLE
        } else {
            binding.progressBarNews.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "NewsActivity"
        private const val q = "cancer"
        private const val category = "health"
        private const val language = "en"
        private const val apiKey = "778b51c9fda6417d8eece26ba0e2d706"
    }
}