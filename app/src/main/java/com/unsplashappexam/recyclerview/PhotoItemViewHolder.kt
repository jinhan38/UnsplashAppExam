package com.unsplashappexam.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unsplashappexam.App
import com.unsplashappexam.model.Photo
import com.unsplashappexam.R
import kotlinx.android.synthetic.main.layout_photo_item.view.*

class PhotoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val photoImageView = itemView.photo_image
    private val photoCreatedAtText = itemView.created_at_text
    private val photoLikesCountText = itemView.likesCountText

    //데이터와 뷰를 묶는다

    fun bindWithView(photoItem: Photo) {
        photoCreatedAtText.text = photoItem.createdAt
        photoLikesCountText.text = photoItem.likesCount.toString()
        Glide.with(App.instance)
            .load(photoItem.thumbnailLink)
            .placeholder(R.drawable.ic_baseline_insert_photo_24)
            .into(photoImageView)

    }
}