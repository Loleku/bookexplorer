package com.example.bookexplorer.data.repository

import com.example.bookexplorer.data.api.OpenLibraryApi
import com.example.bookexplorer.data.local.FavoritesDataStore
import com.example.bookexplorer.data.model.BookWork
import com.example.bookexplorer.data.model.WorkDetailResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull

class BookRepository(private val favoritesDataStore: FavoritesDataStore) {

    private val api: OpenLibraryApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(OpenLibraryApi::class.java)
    }

    suspend fun getFictionBooks(offset: Int): Result<List<BookWork>> {
        return try {
            val response = api.getFictionBooks(offset = offset)
            Result.success(response.works)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookDetail(workId: String): Result<WorkDetailResponse> {
        return try {
            val localBook = favoritesDataStore.getFavorite(workId).firstOrNull()
            if (localBook != null) {
                return Result.success(localBook.detail)
            }
        
            val response = api.getWorkDetail(workId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorite(id: String): Flow<Boolean> = favoritesDataStore.isFavorite(id)
    suspend fun addFavorite(id: String, detail: WorkDetailResponse) = favoritesDataStore.addFavorite(id, detail)
    suspend fun removeFavorite(id: String) = favoritesDataStore.removeFavorite(id)
    
    fun getFavorites(): Flow<List<com.example.bookexplorer.data.model.CachedBook>> = favoritesDataStore.favoritesFlow
}
