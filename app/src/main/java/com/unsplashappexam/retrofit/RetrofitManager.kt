package com.unsplashappexam.retrofit

import android.util.Log
import com.google.gson.JsonElement
import com.unsplashappexam.utils.API
import com.unsplashappexam.utils.Constants.TAG
import com.unsplashappexam.utils.RESPONSE_STATE
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    // http 콜 만들기
    // 레트로핏 인터페이스 가져오기
    private val iRetrofit: IRetrofit? =
        RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)


    fun searchPhotos(searchTerm: String?, completion: (RESPONSE_STATE, String) -> Unit) {
        //searchTerm이 빈값이면, ? 즉 null이면 ""을 넣어주겠다.
        //값이 있으면 그대로 searchTerm을 term에 입력
        val term = searchTerm.let {
            it
        } ?: ""
//          val term = searchTerm ?: ""

        val call = iRetrofit?.searchPhotos(searchTerm = term).let {
            it
        } ?: return
//        val call = iRetrofit?.searchPhotos(searchTerm = term) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "onResponse: ${response.raw()}")
                completion(RESPONSE_STATE.OKAY, response.body().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "onFailure: 응답 실패 : $t")
                completion(RESPONSE_STATE.FAIL, t.toString())
            }

        })


    }
}