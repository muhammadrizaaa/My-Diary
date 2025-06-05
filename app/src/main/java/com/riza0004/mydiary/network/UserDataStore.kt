package com.riza0004.mydiary.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.riza0004.mydiary.dataclass.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preference"
)

class UserDataStore(private val context: Context) {
    companion object{
        private val NAME = stringPreferencesKey("name")
        private val EMAIL = stringPreferencesKey("email")
        private val PHOTO = stringPreferencesKey("photoUrl")
    }

    val userFlow: Flow<User> = context.datastore.data.map {
        User(
            name = it[NAME]?:"",
            email = it[EMAIL]?:"",
            photo = it[PHOTO]?:""
        )
    }

    suspend fun saveData(user: User){
        context.datastore.edit {
            it[NAME] = user.name
            it[EMAIL] = user.email
            it[PHOTO] = user.photo
        }
    }
}