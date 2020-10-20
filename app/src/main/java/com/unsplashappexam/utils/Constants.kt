package com.unsplashappexam.utils

import android.content.Context
import android.widget.Toast

object Constants {
    const val TAG: String = "로그"
}


enum class SEARCH_TYPE {

    PHOTTO,
    USER
}

enum class RESPONSE_STATE{
    OKAY,
    FAIL
}

object API {
    const val BASE_URL = "https://api.unsplash.com/"

    const val CLIENT_ID  = "dKn2HtBtQ5TftryizD40_XjpyRIBUwmYsLiwQ3CuyRE"

    const val SEARCH_PHOTO = "search/photos"
    const val SEARCH_USERS = "search/users"


}

fun toastMethod(context: Context, text : String){
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

}