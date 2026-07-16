package com.example.bukukita.data.model

import com.google.gson.annotations.SerializedName

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val price: Double,
    val description: String?,
    val category: String?,
    @SerializedName("cover_url")
    val coverUrl: String?,
    val stock: Int?,
    @SerializedName("created_at")
    val createdAt: String?
)
