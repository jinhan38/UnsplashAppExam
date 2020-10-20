package com.unsplashappexam

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.unsplashappexam.retrofit.RetrofitManager
import com.unsplashappexam.utils.Constants.TAG
import com.unsplashappexam.utils.RESPONSE_STATE
import com.unsplashappexam.utils.SEARCH_TYPE
import com.unsplashappexam.utils.onMyTextChanged
import com.unsplashappexam.utils.toastMethod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_button_search.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var currentSearchType: SEARCH_TYPE = SEARCH_TYPE.PHOTTO

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: ")

        //라디오 그룹 가져오기

        //매개변수가 있는데 매개변수를 사용하지 않을 때는 _로 바꿀 수 있다.
        search_term_radio_group.setOnCheckedChangeListener { _, checkedID ->
            when (checkedID) {
                R.id.photo_search_radioButton -> {
                    Log.d(TAG, "onCreate: 사진검색")
                    search_term_text_layout.hint = "사진검색"
                    search_term_text_layout.startIconDrawable = resources.getDrawable(
                        R.drawable.ic_baseline_photo_library_24, resources.newTheme()
                    )
                    this.currentSearchType = SEARCH_TYPE.PHOTTO
                }

                R.id.user_search_radioButton -> {
                    Log.d(TAG, "onCreate: 유저검색")
                    search_term_text_layout.hint = "유저검색"
                    search_term_text_layout.startIconDrawable = resources.getDrawable(
                        R.drawable.ic_baseline_person_outline_24, resources.newTheme()
                    )
                    this.currentSearchType = SEARCH_TYPE.USER

                }
            }
            Log.d(TAG, "onCreate: 라디오 변경 $currentSearchType")


        }


        search_term_edit_text.onMyTextChanged {

            if (it != null) {

                if (it.isNotEmpty()) {
                    Log.d(TAG, "onCreate: $it")
                    frame_search_button.visibility = View.VISIBLE
                    search_term_text_layout.helperText = ""

                    //y축을 200만큼 위로 올린다.
                    main_scrollview.scrollTo(0, 250)


                } else {
                    Log.d(TAG, "onCreate: 카운터 0")
                    frame_search_button.visibility = View.INVISIBLE
                    search_term_text_layout.helperText =
                        resources.getString(R.string.login_helper_text)
                }

                if (it.toString().count() == 12) {
                    Log.d(TAG, "onCreate: 검색어 12자 도달")
                    Toast.makeText(this, "검색어는 12자 까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        search_button.setOnClickListener {
            Log.d(TAG, "onCreate: 검색 버튼 클릭 currentSearchType : $currentSearchType")


            Log.d(TAG, "onCreate: 검색 : ${search_term_edit_text.text}")
            //검색 api 호출
            RetrofitManager.instance.searchPhotos(
                searchTerm = search_term_edit_text.toString(),
                completion = { responseState, response ->
                    when (responseState) {
                        RESPONSE_STATE.OKAY -> {
                            toastMethod(this, "api 호출 성공 : $response")
                            Log.d(TAG, "onCreate: $response")
                            
                        }
                        RESPONSE_STATE.FAIL -> {
                            toastMethod(this, "api 호출 에러 : $response")
                            Log.d(TAG, "onCreate: $response")
                        }
                    }

                })
            handleSearchButtonUI(it as Button, progressBar, true)
        }
    }

    private fun handleSearchButtonUI(
        button: Button,
        progressBar: ProgressBar,
        showOrHide: Boolean
    ) {
        when (showOrHide) {

            //검색 버튼 누른 경우
            true -> {
                progressBar.visibility = View.VISIBLE
                button.text = ""
            }

            //작업이 끝나거나 클릭 안한 상태
            else -> {
                progressBar.visibility = View.GONE
                button.text = resources.getString(R.string.searchText)
            }
        }


        Handler().postDelayed({
            progressBar.visibility = View.GONE
            button.text = resources.getString(R.string.searchText)
        }, 1500)
    }

}
