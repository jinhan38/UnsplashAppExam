package com.unsplashappexam.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


//EditText 익스텐션 메소드
//afterTextChanged만 쓰려고 한다
fun EditText.onMyTextChanged(completion : (Editable?) -> Unit) {
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