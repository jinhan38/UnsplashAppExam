package com.unsplashappexam.retrofit

import com.google.gson.JsonElement
import com.unsplashappexam.utils.API
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface IRetrofit {


//    @Headers("Accept-Version: v1", "Authorization: Client-ID ${API.CLIENT_ID}" )
    @GET(API.SEARCH_PHOTO)
    fun searchPhotos(
        @Query("query") searchTerm : String
    ) : Call<JsonElement>
    //Call로 받는 타입정한다. 여기서 만약 custom한 data class가 있으면
    // data class를 넣고, 없으면 JsonElement를 넣으면 된다.


//    @Headers("Accept-Version: v1", "Authorization: Client-ID ${API.CLIENT_ID}" )
    @GET(API.SEARCH_USERS)
    fun searchUsers(
        @Query("query") searchTerm : String
    ) : Call<JsonElement>
    
}