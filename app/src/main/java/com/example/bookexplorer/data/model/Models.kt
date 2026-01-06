package com.example.bookexplorer.data.model

import com.google.gson.annotations.SerializedName

data class SubjectResponse(
    val works: List<BookWork>
)

data class BookWork(
    val key: String,
    val title: String,
    val authors: List<Author>,
    @SerializedName("cover_id") val coverId: Long?
)

data class Author(
    val name: String
)

data class WorkDetailResponse(
    val title: String,
    val description: Any?,
    val subjects: List<String>?,
    val covers: List<Long>?,
    @SerializedName("first_publish_date") val firstPublishDate: String?,
    @SerializedName("number_of_pages") val numberOfPages: Int?
)

fun WorkDetailResponse.getDescriptionString(): String {
    return (when (description) {
        is String -> description
        is List<*> -> (description as List<String>).joinToString("\n")
        else -> "No description"
    })
}

data class CachedBook(
    val id: String,
    val detail: WorkDetailResponse
)
