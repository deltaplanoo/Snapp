package com.campsnap.importer.data

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "camp_snap_settings")

/**
 * Manages persistent preferences using AndroidX DataStore.
 * Persists the camera Storage Access Framework root URI and custom destination album name.
 */
class PreferencesManager(private val context: Context) {

    companion object {
        val KEY_TREE_URI = stringPreferencesKey("camp_snap_tree_uri")
        val KEY_DESTINATION_ALBUM = stringPreferencesKey("camp_snap_destination_album")
        const val DEFAULT_DESTINATION_ALBUM = "Camp Snap"
    }

    val treeUriFlow: Flow<Uri?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_TREE_URI]?.let { Uri.parse(it) }
        }

    val destinationAlbumFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_DESTINATION_ALBUM] ?: DEFAULT_DESTINATION_ALBUM
        }

    suspend fun saveTreeUri(uri: Uri) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TREE_URI] = uri.toString()
        }
    }

    suspend fun clearTreeUri() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TREE_URI)
        }
    }

    suspend fun saveDestinationAlbum(name: String) {
        val cleanName = name.trim().ifEmpty { DEFAULT_DESTINATION_ALBUM }
        context.dataStore.edit { preferences ->
            preferences[KEY_DESTINATION_ALBUM] = cleanName
        }
    }
}
