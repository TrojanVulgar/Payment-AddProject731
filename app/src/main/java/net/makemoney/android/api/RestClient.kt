package net.makemoney.android.api

import net.makemoney.android.BuildConfig
import net.makemoney.android.api.RestClient.api
import net.makemoney.android.utils.AppPreferences
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Class provides access to do requests.
 * Use [api] value to send one of the requests described in [Api] class.
 * In this representation you're not allowed to change end point on the fly
 */
object RestClient {

    private const val END_POINT = "https://cashrewards.space/api/" //TODO: put here your server endPoint
    private const val TIMEOUT = 20L
    const val MAX_REQUEST_COUNT = 3

    val api: net.makemoney.android.api.Api = net.makemoney.android.api.RestClient.provideRetrofit().create(net.makemoney.android.api.Api::class.java)

    //Used to send several requests in parallel
    private val dispatcher: Dispatcher
        get() {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = net.makemoney.android.api.RestClient.MAX_REQUEST_COUNT
            return dispatcher
        }

    private val authInterceptor: Interceptor
        get() = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                    .header("accept", "application/json")
                    .header("token", AppPreferences.token)
                    .method(original.method(), original.body())
                    .build()
            chain.proceed(request)
        }

    private val loggingInterceptor: HttpLoggingInterceptor
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return interceptor
        }

    private fun provideRetrofit(): Retrofit {
        val builder = OkHttpClient.Builder()
                .connectTimeout(net.makemoney.android.api.RestClient.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(net.makemoney.android.api.RestClient.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(net.makemoney.android.api.RestClient.TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(net.makemoney.android.api.RestClient.authInterceptor)
                .dispatcher(net.makemoney.android.api.RestClient.dispatcher)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(net.makemoney.android.api.RestClient.loggingInterceptor)
        }
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(CoroutineCallAdapterFactory()) //used only with coroutines
                .baseUrl(net.makemoney.android.api.RestClient.END_POINT)
                .client(builder.build())
                .build()
    }

}
