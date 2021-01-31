package halla.icsw.acca_kotlin

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Retrofit_Interface {
    @FormUrlEncoded
    @POST("/api/aroundAll.do")
    fun getOilStationInfo(
        @Field("code") code: String,
        @Field("x") x: Double,
        @Field("y") y: Double,
        @Field("radius") radius: Int,
        @Field("sort") sort: Int,
        @Field("prodcd") prodcd: String,
        @Field("out") out: String
    ): Call<Result>

    companion object{
        private const val BASE_URL = "https://www.opinet.co.kr/"
        fun create(): Retrofit_Interface{

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build()


            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Retrofit_Interface::class.java)
        }
    }
}