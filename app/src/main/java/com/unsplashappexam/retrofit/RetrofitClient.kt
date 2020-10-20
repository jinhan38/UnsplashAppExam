package com.unsplashappexam.retrofit

import android.util.Log
import com.unsplashappexam.utils.API
import com.unsplashappexam.utils.Constants.TAG
import com.unsplashappexam.utils.isJsonArray
import com.unsplashappexam.utils.isJsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//코틀린에서 object는 싱글톤이다. 메모리를 하나만 쓴다

object RetrofitClient {
    // 레트로핏 클라이언트 선언

    private var retrofitClient: Retrofit? = null

    //레트로핏 클라이언트 가져오기
    //반환값이 있을 수도 있고, 없을 수도 있고
    fun getClient(baseUrl: String): Retrofit? {
        Log.d(TAG, "getClient: ")

        //okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        //로깅 인터셉터 추가, 모든 통신 내용을 볼 수 있다
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "log: 로깅 인터셉터 : $message")

            when {
                message.isJsonObject() -> {
                    Log.d(TAG, JSONObject(message).toString(4))
                }
                message.isJsonArray() -> {
                    Log.d(TAG, JSONObject(message).toString(4))
                }
                else -> {
                    try {
                        Log.d(TAG, JSONObject(message).toString(4))
                    } catch (e: Exception) {
                        Log.d(TAG, "getClient: message : $e")
                    }
                }
            }
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 위에서 생성한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)


        // 기본 파라메터 인터셉터 설정
        val baseParameterInterceptor: Interceptor = (Interceptor { chain ->
            Log.d(TAG, "intercept: ")
            //오리지널 리퀘스트를 가져옴
            val originalRequest = chain.request()

            //쿼리 파라메터 추가하기
            val addedUrl =
                originalRequest.url.newBuilder().addQueryParameter("client_id", API.CLIENT_ID)
                    .build()
            val fianlRequest = originalRequest.newBuilder()
                .url(addedUrl)
                .method(originalRequest.method, originalRequest.body)
                .build()

            chain.proceed(fianlRequest)
        })

        //위에서 설정한 기본파라메터 인터셉터를 okhttp 클라이언트에 추가한다
        client.addInterceptor(baseParameterInterceptor)

        //커넥션 타임아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        //기본 파라메터 추가
        if (retrofitClient == null) {
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //위에서 설정한 클라이언트로 레트로핏 클라이언트를 설정한다.
                .client(client.build())
                .build()
        }

        return retrofitClient

    }

}