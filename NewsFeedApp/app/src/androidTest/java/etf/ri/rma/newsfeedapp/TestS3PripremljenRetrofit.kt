package etf.ri.rma.newsfeedapp

import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
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
        val newsApiService = retrofit.create(NewsApiService::class.java)
        val newsDAO = NewsDAO.getInstance()
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
        val imagaApiService = retrofit.create(ImagaApiService::class.java)
        val imagaDAO = ImagaDAO.getInstance()
        imagaDAO.setApiService(imagaApiService)
        return imagaDAO
    }
}