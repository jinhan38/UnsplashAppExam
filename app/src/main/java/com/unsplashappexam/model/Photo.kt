package com.unsplashappexam.model

import java.io.Serializable

data class Photo(
    var author: String,
    var likesCount: Int,
    var thumbnailLink: String,
    var createdAt: String
) : Serializable {
}