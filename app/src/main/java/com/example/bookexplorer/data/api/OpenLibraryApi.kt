package com.example.bookexplorer.data.api

import com.example.bookexplorer.data.model.SearchResponse
import com.example.bookexplorer.data.model.SubjectResponse
import com.example.bookexplorer.data.model.WorkDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {

    @GET("subjects/fiction.json")
    suspend fun getFictionBooks(@Query("limit") limit: Int = 30, @Query("offset") offset: Int = 0): SubjectResponse

    @GET("search.json")
    suspend fun searchBooks(@Query("q") query: String, @Query("page") page: Int = 1): SearchResponse

    @GET("works/{workId}.json")
    suspend fun getWorkDetail(@Path("workId") workId: String): WorkDetailResponse
}
