package com.example.bookexplorer.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites_prefs")

class FavoritesDataStore(private val context: Context) {
    private val FAVORITE_BOOKS_KEY = androidx.datastore.preferences.core.stringPreferencesKey("favorite_books_json")
    private val gson = com.google.gson.Gson()

    val favoritesFlow: Flow<List<com.example.bookexplorer.data.model.CachedBook>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[FAVORITE_BOOKS_KEY]
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                try {
                    val type = object : com.google.gson.reflect.TypeToken<List<com.example.bookexplorer.data.model.CachedBook>>() {}.type
                    gson.fromJson(json, type)
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }

    suspend fun addFavorite(id: String, detail: com.example.bookexplorer.data.model.WorkDetailResponse) {
        context.dataStore.edit { preferences ->
            val currentList = try {
                val json = preferences[FAVORITE_BOOKS_KEY]
                if (json.isNullOrEmpty()) {
                    emptyList()
                } else {
                    val type = object : com.google.gson.reflect.TypeToken<List<com.example.bookexplorer.data.model.CachedBook>>() {}.type
                    gson.fromJson<List<com.example.bookexplorer.data.model.CachedBook>>(json, type)
                }
            } catch (e: Exception) {
                emptyList()
            }

            val newList = currentList.filter { it.id != id } + com.example.bookexplorer.data.model.CachedBook(id, detail)
            preferences[FAVORITE_BOOKS_KEY] = gson.toJson(newList)
        }
    }

    suspend fun removeFavorite(id: String) {
        context.dataStore.edit { preferences ->
            val currentList = try {
                val json = preferences[FAVORITE_BOOKS_KEY]
                if (json.isNullOrEmpty()) {
                    emptyList()
                } else {
                    val type = object : com.google.gson.reflect.TypeToken<List<com.example.bookexplorer.data.model.CachedBook>>() {}.type
                    gson.fromJson<List<com.example.bookexplorer.data.model.CachedBook>>(json, type)
                }
            } catch (e: Exception) {
                emptyList()
            }
            
            val newList = currentList.filter { it.id != id }
            preferences[FAVORITE_BOOKS_KEY] = gson.toJson(newList)
        }
    }

    fun isFavorite(id: String): Flow<Boolean> {
        return favoritesFlow.map { list -> list.any { it.id == id } }
    }
    
    fun getFavorite(id: String): Flow<com.example.bookexplorer.data.model.CachedBook?> {
        return favoritesFlow.map { list -> list.find { it.id == id } }
    }
}
