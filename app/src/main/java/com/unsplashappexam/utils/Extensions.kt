package com.unsplashappexam.utils

import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.unsplashappexam.utils.Constants.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.*


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


//날짜 포맷
@SuppressLint("SimpleDateFormat")
fun Date.changeToString(): String {
    val format = SimpleDateFormat("HH:mm:ss")
    return format.format(this)
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


//에딧텍스트의 텍스트 변경을 flow로 받기
@ExperimentalCoroutinesApi
fun EditText.textChangeToFlow(): Flow<CharSequence?> {

    return callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "onTextChanged: text : $p0")
                //값 내보내기
                offer(text)
            }

        }

        addTextChangedListener(listener)

        //콜백이 사라질 때 실행되는 메소드
        //onDestory에서 context cancel을 달아놔서 그때 호출됨
       awaitClose {
           Log.d(TAG, "textChangeToFlow: awaitClose ")
           removeTextChangedListener(listener)
        }

    }.onStart {

        Log.d(TAG, "textChangeToFlow: / onStart 발동")

        //Rx 에서 onNext와 동일
        // emit으로 이벤트 전달
        emit(text)

    }
}