package com.dicoding.asclepius.api.retrofit
import com.dicoding.asclepius.api.response.NewsResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("Authorization: token 778b51c9fda6417d8eece26ba0e2d706")
    @GET("top-headlines")
    fun getNews(
        @Query("q") q: String,
        @Query("category") category: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>
}