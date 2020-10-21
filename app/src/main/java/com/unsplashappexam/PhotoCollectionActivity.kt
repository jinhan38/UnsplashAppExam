package com.unsplashappexam

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import com.unsplashappexam.model.Photo
import com.unsplashappexam.model.SearchData
import com.unsplashappexam.recyclerview.ISearchHistoryRecyclerView
import com.unsplashappexam.recyclerview.PhotoGridRecyclerViewAdapter
import com.unsplashappexam.recyclerview.SearchHistoryRecyclerViewAdapter
import com.unsplashappexam.retrofit.RetrofitManager
import com.unsplashappexam.utils.*
import com.unsplashappexam.utils.Constants.TAG
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo_collection.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class PhotoCollectionActivity : AppCompatActivity(),
    SearchView.OnQueryTextListener,
    CompoundButton.OnCheckedChangeListener,
    View.OnClickListener,
    ISearchHistoryRecyclerView {

    private var photoList = ArrayList<Photo>()

    //검색기록 배열
    private var searchHistoryList = ArrayList<SearchData>()      


    //서치뷰
    private lateinit var mySearchView: SearchView

    //서치뷰 EditText
    private lateinit var mySearchViewEditText: EditText

    /*
    //rxJava 적용
    private var myCompositeDisposable = CompositeDisposable()
*/

    private var myCoroutineJob: Job = Job()
    private val myCoroutineContext: CoroutineContext
        get() = Dispatchers.IO + myCoroutineJob

    //어댑터
    //메모리에 나중에 올라감
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter
    private lateinit var searchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter

    private var searchTerm: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)

        val bundle = intent.getBundleExtra("array_bundle")
        searchTerm = intent.getStringExtra("search_term").toString()
        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>


        search_history_mode_switch.setOnCheckedChangeListener(this)
        clear_search_history_button.setOnClickListener(this)

        //앱바 설정 부분
        setSupportActionBar(topAppBar)   //앱바 설정을 할 수 있게 만듬
        photoRecyclerViewSetting(photoList, my_photo_recyclerView, searchTerm)


        //저장된 검색기록 가져오기
        this.searchHistoryList = SharedPrefManager.getSearchHistoryList() as ArrayList<SearchData>
        this.searchHistoryList.forEach {
            Log.d(TAG, "onCreate: 저장된 검색기록 확인 ${it.term}, 시간 : ${it.timeStamp}")
        }

        searchHistoryRecyclerViewSetting(this.searchHistoryList, search_history_recycler_view)

        handleSearchViewUI()

        search_history_mode_switch.isChecked = SharedPrefManager.getSearchHistoryMode()

        if (searchTerm.count() > 0) {
            this.insertSearchTermHistory(searchTerm)
        }
    }


    //photo Grid 리사이클러 뷰 세팅
    private fun photoRecyclerViewSetting(
        photoList: ArrayList<Photo>,
        recyclerView: RecyclerView,
        appbarTerm: String
    ) {

        topAppBar.title = appbarTerm
        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter()
        this.photoGridRecyclerViewAdapter.submitList(photoList)

        //GridLayoutManager 두번째 매개변수가 span카운트, 즉 가로의 개로
        val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.apply {
            this.layoutManager = gridLayoutManager
            adapter = photoGridRecyclerViewAdapter
        }


    }


    //검색기록 리사이클러 뷰 세팅
    private fun searchHistoryRecyclerViewSetting(
        searchHistoryList: ArrayList<SearchData>,
        recyclerView: RecyclerView
    ) {

        Log.d(TAG, "searchHistoryRecyclerViewSetting: ")
        this.searchHistoryRecyclerViewAdapter = SearchHistoryRecyclerViewAdapter(this)
        this.searchHistoryRecyclerViewAdapter.submitList(searchHistoryList)

        //최근 검색어가 위로 오도록
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        recyclerView.apply {
            layoutManager = linearLayoutManager
            this.scrollToPosition(searchHistoryRecyclerViewAdapter.itemCount - 1)
            adapter = searchHistoryRecyclerViewAdapter
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: ")
        val inflater = menuInflater
        inflater.inflate(R.menu.top_app_bar_menu, menu)
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        this.mySearchView = menu?.findItem(R.id.search_menu)?.actionView as SearchView
        this.mySearchView.apply {
            this.queryHint = "검색어를 입력해주세요"

            this.setOnQueryTextListener(this@PhotoCollectionActivity)

            this.setOnQueryTextFocusChangeListener { _, hasExpanded ->
                when (hasExpanded) {
                    true -> {
//                        linear_search_history_view.visibility = View.VISIBLE
                        Log.d(TAG, "onCreateOptionsMenu: 서치뷰 오픈")
                        handleSearchViewUI()
                    }
                    false -> {
                        linear_search_history_view.visibility = View.GONE
                        Log.d(TAG, "onCreateOptionsMenu: 서치뷰 닫힘")
                    }

                }
            }

            mySearchViewEditText = this.findViewById(R.id.search_src_text)
//            mySearchViewEditText = this.findViewById(android.widget.R.id.search_src_text)


            /*     rx 적용
               //에딧텍스트 옵저버블
               //글자가 담겨있는 옵저버블이 된다
               //에딧 텍스트의 변화 과정을 지켜보면서 그 결과를 반환한다.
               val editTextChangeObservable = mySearchViewEditText.textChanges()
               val searchEditTextSubscription: Disposable =
                   //옵저버블에 연산자 추가
                   editTextChangeObservable
                       // 글자가 입력 되고 나서 0.8초 후에 onNext이벤트로 흘러감
                       //글자 입력이 중단되고 1500후에 실행됨
                       .debounce(1000, TimeUnit.MILLISECONDS)
                       // IO 쓰레드에서 돌리겠다.
                       // Scheduler instance
                       //네트워크 요청, 파일 읽기, 쓰기, 디비처리 등
                       .subscribeOn(Schedulers.io())
                       // 구독을 통해 이벤트 응답 받기
                       .subscribeBy(
                           onNext = {
                               Log.d(TAG, "RX onNext: $it")
                               //TODO:: 흘러들어온 이벤트 데이터로 api 호출
                               if (it.isNotEmpty()) {
                                   searchPhotoApiCall(it.toString())
                               }
                           },
                           onComplete = {
                               Log.d(TAG, "RX onComplete")
                           },
                           onError = {
                               Log.d(TAG, "RX onError")
                           }
                       )

               // compositeDisposable에 추가
               myCompositeDisposable.add(searchEditTextSubscription)*/

            // RX의  스케줄러와 비슷, IO 쓰레드에서 실행
//            GlobalScope.launch(context = Dispatchers.IO) {
            GlobalScope.launch(context = myCoroutineContext) {

                //editText가 변경되었을 때
                val editTextFlow = mySearchViewEditText.textChangeToFlow()
                editTextFlow
                    .debounce(1500)
                    .filter {
                        //filter는 rx에도 존재
                        //filter는 true, false로 값을 받는다.
                        it?.length!! > 0
                    }
                    .onEach {
                        //여기서 텍스트 처리하면 됨
                        Log.d(TAG, "onCreateOptionsMenu: flow로 받는다 $it")
                    }.launchIn(this)

            }

            // destroy에서 disposable clear 잊지 말기


        }

        this.mySearchViewEditText.apply {
            this.filters = arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }

        return true
    }


    //서치뷰 검색어 입력 이벤트
    override fun onQueryTextSubmit(p0: String?): Boolean {
        if (!p0.isNullOrEmpty()) {
            this.topAppBar.title = p0

            //TODO:: api 호출
            //TODO:: 검색어 저장
            //검색 api 호출

            searchPhotoApiCall(p0)
            this.insertSearchTermHistory(p0)

        }
        Log.d(TAG, "onQueryTextSubmit: 소프트키보드의 검색 버튼 클릭")
//        this.mySearchView.setQuery("", false)
//        this.mySearchView.clearFocus()//키보드 내려감
        this.topAppBar.collapseActionView()
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        Log.d(TAG, "onQueryTextChange: 텍스트 입력중 $p0")
        val userInputText = p0.let {
            it
        } ?: ""

        if (userInputText.count() == 12) {
            Toast.makeText(this, "검색어는 12자 까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
        }

//        if (userInputText.count() in 1..12) {
//            searchPhotoApiCall(userInputText)
//
//        }

        return true
    }

    //searchView의 switch 리스너
    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
        when (switch) {
            search_history_mode_switch -> {
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: 저장 활성화")
                    SharedPrefManager.setSearchHistoryMode(true)
                } else {
                    Log.d(TAG, "onCheckedChanged: 저장 비활성화")
                    SharedPrefManager.setSearchHistoryMode(false)
                }
            }
        }

    }

    override fun onClick(view: View?) {
        when (view) {
            clear_search_history_button -> {
                //쉐어드에 저장된 것 지우고
                SharedPrefManager.clearSearchHistoryList()
                //현재 activity의 historyList의 데이터도 지운다
                this.searchHistoryList.clear()
                this.searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
                handleSearchViewUI()
                Log.d(TAG, "onClick: 검색기록 삭제 버튼 클릭")
            }
        }

    }


    override fun onSearchItemDeleteClicked(position: Int) {
        //TODO::해당 번째의 녀석을 삭제
        Log.d(TAG, "onSearchItemDeleteClicked: $position")
        this.searchHistoryList.removeAt(position)

        //shared는 덮어 쓰는 형식이다
        //해당 요소 삭제하고 데이터 덮어쓰기
        SharedPrefManager.storeSearchHistoryList(this.searchHistoryList)
        this.searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
        handleSearchViewUI()
    }

    override fun onSearchItemClicked(position: Int) {
        Log.d(TAG, "onSearchItemClicked: $position")

        //TODO::해당 번째의 녀석으로 다시 api 호출

        //검색 api 호출
        val userSearchInput = this.searchHistoryList[position].term

        searchPhotoApiCall(userSearchInput)
        this.insertSearchTermHistory(userSearchInput)
        this.topAppBar.title = userSearchInput
        this.topAppBar.collapseActionView()


    }


    /**
     * 검색 api 호출
     */
    private fun searchPhotoApiCall(query: String) {

        RetrofitManager.instance.searchPhotos(
            searchTerm = query,
            completion = { responseState, response ->
                when (responseState) {
                    RESPONSE_STATUS.OKAY -> {
                        Log.d(TAG, "onCreate: ${response?.size}")

                        if (response != null) {
                            this.photoList.clear()
                            this.photoList = response
                            this.photoGridRecyclerViewAdapter.submitList(this.photoList)
                            this.photoGridRecyclerViewAdapter.notifyDataSetChanged()
                        }


                    }

                    RESPONSE_STATUS.NO_CONTENT -> {
                        toastMethod(this, "검색 결과가 없습니다.")
                    }

                    RESPONSE_STATUS.FAIL -> {
                        toastMethod(this, "api 호출 에러 : $response")
                        Log.d(TAG, "onCreate: $response")
                    }
                }
            })
    }


    /**
     * searchView 활성화 여부 설정
     */
    private fun handleSearchViewUI() {
        Log.d(TAG, "handleSearchViewUI: ")
        if (this.searchHistoryList.size > 0) {
            search_history_recycler_view.visibility = View.VISIBLE
            search_history_recycler_view_label.visibility = View.VISIBLE
            clear_search_history_button.visibility = View.VISIBLE
        } else {
            search_history_recycler_view.visibility = View.INVISIBLE
            search_history_recycler_view_label.visibility = View.INVISIBLE
            clear_search_history_button.visibility = View.INVISIBLE
        }
    }


    // 검색어 저장
    /**
     * 검색어 shared에 저장
     * 기존 데이터 중복 체크
     */
    private fun insertSearchTermHistory(searchTerm: String) {
        Log.d(TAG, "insertSearchTermHistory: ")

        if (SharedPrefManager.getSearchHistoryMode()) {

            //중복 아이템 삭제
            var indexListToRemove = ArrayList<Int>()
            this.searchHistoryList.forEachIndexed { index, searchDataItem ->
                Log.d(TAG, "insertSearchTermHistory: $index")
                if (searchDataItem.term == searchTerm) {
                    indexListToRemove.add(index)
                }
            }
            indexListToRemove.forEach {
                this.searchHistoryList.removeAt(it)
            }

            // 새 아이템 넣기
            val newSearchData = SearchData(term = searchTerm, timeStamp = Date().changeToString())
            this.searchHistoryList.add(newSearchData)
            this.searchHistoryRecyclerViewAdapter.notifyDataSetChanged()

            //쉐어드에 덮어쓰기
            SharedPrefManager.storeSearchHistoryList(this.searchHistoryList)


        } else {

        }
    }


    override fun onDestroy() {

        //코루틴 적용
        myCoroutineContext.cancel()
        //rx 적용
//        this.myCompositeDisposable.clear()
        super.onDestroy()
    }
}