package com.unsplashappexam.recyclerview

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.unsplashappexam.model.SearchData
import com.unsplashappexam.utils.Constants.TAG
import kotlinx.android.synthetic.main.layout_search_history_item.view.*

class SearchHistoryItemViewHolder(
    itemView: View,
    searchRecyclerViewInterface: ISearchHistoryRecyclerView
) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {


    private val search_history_item_wrap = itemView.search_history_item_wrap
    private val search_history_term_text = itemView.search_history_term_text
    private val search_history_date_text = itemView.search_history_date_text
    private val search_history_delete_button = itemView.search_history_delete_button
    private var iSearchHistoryRecyclerView: ISearchHistoryRecyclerView? = null

    init {
        this.iSearchHistoryRecyclerView = searchRecyclerViewInterface
        search_history_item_wrap.setOnClickListener(this)
        search_history_delete_button.setOnClickListener(this)
    }


    //데이터와 뷰를 묶는다
    fun bindWithView(searchData: SearchData) {
        search_history_term_text.text = searchData.term
        search_history_date_text.text = searchData.timeStamp


    }

    override fun onClick(view: View?) {
        when (view) {
            search_history_item_wrap -> {
                Log.d(TAG, "onClick: search_history_item_wrap click")
                this.iSearchHistoryRecyclerView?.onSearchItemClicked(position = adapterPosition)

            }
            search_history_delete_button -> {
                this.iSearchHistoryRecyclerView?.onSearchItemDeleteClicked(position = adapterPosition)
                Log.d(TAG, "onClick: search_history_delete_button click")

            }
        }
    }
}