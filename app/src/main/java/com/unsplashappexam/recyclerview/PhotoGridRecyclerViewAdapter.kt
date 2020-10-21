package com.unsplashappexam.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unsplashappexam.model.Photo
import com.unsplashappexam.R

class PhotoGridRecyclerViewAdapter : RecyclerView.Adapter<PhotoItemViewHolder>() {

    private var photoList = ArrayList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {

        return PhotoItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_photo_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {

        holder.bindWithView(photoList[position])
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    fun submitList(photoList : ArrayList<Photo>){
        this.photoList = photoList
    }
}