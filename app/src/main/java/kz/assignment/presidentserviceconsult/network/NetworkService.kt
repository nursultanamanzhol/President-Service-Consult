package kz.assignment.presidentserviceconsult.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService private constructor() {

    // Объект для обращения к API
    val gitHubApi: GitHubApi

    init {
        // Инициализация Retrofit или другой библиотеки для работы с API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        gitHubApi = retrofit.create(GitHubApi::class.java)
    }

    companion object {
        @Volatile
        private var instance: NetworkService? = null

        fun getInstance(): NetworkService {
            return instance ?: synchronized(this) {
                instance ?: NetworkService().also { instance = it }
            }
        }
    }
}

