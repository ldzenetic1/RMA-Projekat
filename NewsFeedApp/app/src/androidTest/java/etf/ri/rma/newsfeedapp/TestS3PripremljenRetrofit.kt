package etf.ri.rma.newsfeedapp

import etf.ri.rma.newsfeedapp.data.network.ImagaDAO // Uvezi izmijenjeni ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO   // Uvezi izmijenjeni NewsDAO
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService // Uvezi vaše Imagga API sučelje
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService   // Uvezi vaše News API sučelje
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestS3PripremljenRetrofit {

    fun getNewsDAOwithBaseURL(baseURL: String, httpClient: OkHttpClient): NewsDAO {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Kreirajte servis za mock server
        val newsApiService = retrofit.create(NewsApiService::class.java)

        // Dohvatite instancu NewsDAO (preko getInstance() za Singleton ponašanje
        // ili kreirajte novu instancu ako vam je potrebno za test)
        val newsDAO = NewsDAO.getInstance() // Koristi getInstance() ako želite Singleton
        // Val newsDAO = NewsDAO() // Koristite ovo ako želite novu instancu za svaki test
        newsDAO.setApiService(newsApiService)
        newsDAO.clearCacheForTesting()
        return newsDAO
    }

    fun getImaggaDAOwithBaseURL(baseURL: String, httpClient: OkHttpClient): ImagaDAO {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Kreirajte servis za mock server
        val imagaApiService = retrofit.create(ImagaApiService::class.java)

        // Dohvatite instancu ImagaDAO
        val imagaDAO = ImagaDAO.getInstance() // Koristi getInstance() ako želite Singleton
        // val imagaDAO = ImagaDAO() // Koristite ovo ako želite novu instancu za svaki test
        imagaDAO.setApiService(imagaApiService)
        return imagaDAO
    }
}