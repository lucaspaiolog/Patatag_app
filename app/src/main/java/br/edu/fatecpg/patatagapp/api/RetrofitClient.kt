package br.edu.fatecpg.patatagapp.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // IMPORTANTE:
    // Se usar emulador: use "http://10.0.2.2:5000/"
    // Se usar celular real: use o IP do seu PC, ex: "http://192.168.1.X:5000/"
    const val BASE_URL = "http://192.168.15.73:5000/"

    // Gerenciador de Cookies (Necessário porque o Flask usa sessão de login)
    private val cookieJar = object : CookieJar {
        private val cookieStore = HashMap<String, List<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: ArrayList()
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar) // Adiciona suporte a cookies
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Mostra logs detalhados no Logcat
        })
        .build()

    val instance: PatatagApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PatatagApiService::class.java)
    }
}