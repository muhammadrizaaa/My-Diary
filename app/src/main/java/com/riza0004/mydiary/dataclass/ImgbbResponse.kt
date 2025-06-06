package com.riza0004.mydiary.dataclass

data class ImgbbResponse(
    val data: ImgbbData,
    val success: Boolean,
    val status: Int
)

data class ImgbbData(
    val url: String,
    val display_url: String,
    val delete_url: String
)

