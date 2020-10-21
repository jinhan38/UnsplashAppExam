package com.unsplashappexam.retrofit

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.JsonElement
import com.unsplashappexam.model.Photo
import com.unsplashappexam.utils.API
import com.unsplashappexam.utils.Constants.TAG
import com.unsplashappexam.utils.RESPONSE_STATUS
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    // http 콜 만들기
    // 레트로핏 인터페이스 가져오기
    private val iRetrofit: IRetrofit? =
        RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)


    fun searchPhotos(
        searchTerm: String?,
        completion: (RESPONSE_STATUS, ArrayList<Photo>?) -> Unit
    ) {
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

        val parsePhotoArrayList = ArrayList<Photo>()

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                when (response.code()) {
                    200 -> {
                        //response.body의 데이터가 있다면
                        response.body()?.let {

                            val body = it.asJsonObject
                            val total = body.get("total").asInt
                            val total_pages = body.get("total_pages").asInt
                            val result = body.getAsJsonArray("results")

                            result.forEach { resultItem ->
                                val resultItemObject = resultItem.asJsonObject
                                val user = resultItemObject.get("user").asJsonObject
                                val username: String = user.get("username").asString
                                val likesCount: Int = resultItemObject.get("likes").asInt
                                val thumbnailLink =
                                    resultItemObject.get("urls").asJsonObject.get("thumb").asString
                                val createdAt = resultItemObject.get("created_at").asString
                                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                val formatter = SimpleDateFormat("yyyy년\nMM월 dd일")
                                val outputDateString = formatter.format(parser.parse(createdAt))
//                                Log.d(TAG, "onResponse: outputDateString : $outputDateString")

                                val photoItem = Photo(
                                    author = username,
                                    likesCount = likesCount,
                                    thumbnailLink = thumbnailLink,
                                    createdAt = outputDateString
                                )

                                parsePhotoArrayList.add(photoItem)

                            }
                            Log.d(TAG, "onResponse: total : $total, total_pages : $total_pages")


                            if (total == 0) {//검색한 텍스트에 대한 데이터가 없으면 no_content로 보낸다.
                                completion(RESPONSE_STATUS.NO_CONTENT, null)
                            } else {//데이터가 있다면

                                completion(RESPONSE_STATUS.OKAY, parsePhotoArrayList)
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "onFailure: 응답 실패 : $t")
                completion(RESPONSE_STATUS.FAIL, null)
            }

        })


    }
}