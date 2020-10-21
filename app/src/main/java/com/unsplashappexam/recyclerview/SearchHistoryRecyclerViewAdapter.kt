package com.unsplashappexam.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsplashappexam.R
import com.unsplashappexam.model.Photo
import com.unsplashappexam.model.SearchData

//매개변수로 인터페이스를 넣었다
class SearchHistoryRecyclerViewAdapter(searchHistoryRecyclerViewInterface: ISearchHistoryRecyclerView) :
    RecyclerView.Adapter<SearchHistoryItemViewHolder>() {

    private var searchHistoryList = ArrayList<SearchData>()

    private var iSearchHistoryRecyclerView : ISearchHistoryRecyclerView? = null

    //매개변수가 들어온 다음에 init이 실행된다.
    //인터페이스를 연결시켜주고, viewholder와도 연결시켜줘야 한다
    init {
        this.iSearchHistoryRecyclerView = searchHistoryRecyclerViewInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryItemViewHolder {
        return SearchHistoryItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_search_history_item, parent, false), this.iSearchHistoryRecyclerView!!
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryItemViewHolder, position: Int) {

        holder.bindWithView(searchData = this.searchHistoryList[position])
    }

    override fun getItemCount(): Int {
        return this.searchHistoryList.size
    }

    fun submitList(searchHistoryList: ArrayList<SearchData>) {
        this.searchHistoryList = searchHistoryList
    }
}