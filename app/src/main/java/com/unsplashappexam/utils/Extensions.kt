package com.unsplashappexam.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


//문자열이 제이슨 형태인지

fun String?.isJsonObject(): Boolean = this?.startsWith("{") == true && this.endsWith("}")

//fun String?.isJsonObject(): Boolean {
////    if (this?.startsWith("{") == true && this.endsWith("}")) {
////        return true
////    } else {
////        return false
////    }
//    return this?.startsWith("{") == true && this.endsWith("}")
//
//}


//fun String?.isJsonObject(): Boolean {
//    return this?.startsWith("{") == true && this.endsWith("}")
//}


// 제이슨 배열 형태인지 구분
fun String?.isJsonArray(): Boolean {
    return this?.startsWith("[") == true && this.endsWith("]")
}

//EditText 익스텐션 메소드
//afterTextChanged만 쓰려고 한다
fun EditText.onMyTextChanged(completion: (Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }


    })

}