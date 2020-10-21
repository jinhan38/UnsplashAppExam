package com.unsplashappexam.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.unsplashappexam.App
import com.unsplashappexam.model.SearchData
import com.unsplashappexam.utils.Constants.TAG

object SharedPrefManager {

    private const val SHARED_SEARCH_HISTORY = "shared_search_history"
    private const val KEY_SEARCH_HISTORY = "key_search_history"

    private const val SHARED_SEARCH_HISTORY_MODE = "shared_search_history_mode"
    private const val KEY_SEARCH_HISTORY_MODE = "key_search_history_mode"


    //검색어 저장 모드 설정하기
    fun setSearchHistoryMode(isActivated: Boolean) {
        Log.d(TAG, "setSearchHistoryMode: $isActivated")
        //쉐어드 가져오기
        val shared =
            App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        //쉐어드 에디터 가져오기
        val editor = shared.edit()
        editor.putBoolean(KEY_SEARCH_HISTORY_MODE, isActivated)
        editor.apply()
    }

    //검색어 저장 모드 확인하기
    fun getSearchHistoryMode(): Boolean {
        //쉐어드 가져오기
        val shared =
            App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)
        val isSearchHistoryMode = shared.getBoolean(KEY_SEARCH_HISTORY_MODE, false)!!

        Log.d(TAG, "getSearchHistoryMode: $isSearchHistoryMode")
        return isSearchHistoryMode
    }


    //검색 목록 저장
    //배열 상태의 것을 저장할 것이기 때문에 GSON을 이용해 문자열로 만들고 shared에 저장
    fun storeSearchHistoryList(searchHistoryList: MutableList<SearchData>) {
        Log.d(TAG, "storeSearchHistoryList: $searchHistoryList")

        //매개변수로 들어온 배열을 문자열로 변환
        val searchHistoryListString: String = Gson().toJson(searchHistoryList)
        Log.d(TAG, "storeSearchHistoryList: 스트링 변환 $searchHistoryListString")

        //쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        //쉐어드 에디터 가져오기
        val editor = shared.edit()
        editor.putString(KEY_SEARCH_HISTORY, searchHistoryListString)
        editor.apply()
    }

    fun getSearchHistoryList(): MutableList<SearchData> {
        //쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        val storedSearchHistoryListString = shared.getString(KEY_SEARCH_HISTORY, "")!!

        var storedSearchHistoryList = ArrayList<SearchData>()

        // 검색 목록 값이 있다면
        if (storedSearchHistoryListString.isNotEmpty()) {

            //저장된 문자열을 객체 배열로 변경
            storedSearchHistoryList =
                Gson().fromJson(storedSearchHistoryListString, Array<SearchData>::class.java)
                    .toMutableList() as ArrayList<SearchData>

        }

        return storedSearchHistoryList
    }


    //검색 목록 전체 지우기
    fun clearSearchHistoryList() {
        Log.d(TAG, "clearSearchHistoryList: ")

        //쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        //가져온 쉐어드의 데이터 삭제하기
        val editor = shared.edit()
        editor.clear()
        editor.apply()
    }

}