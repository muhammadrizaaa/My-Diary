package com.riza0004.mydiary.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riza0004.mydiary.dataclass.Notes
import com.riza0004.mydiary.network.ApiStatus
import com.riza0004.mydiary.network.NotesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    var data = mutableStateOf(emptyList<Notes>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    fun retrieveData(idUser: String){
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = NotesApi.services.getNotes(idUser)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception){
                Log.d("GET_NOTES_ERR", "Err: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }
    fun saveData(
        notes: Notes
    ){
        viewModelScope.launch {
            try{
                val result = NotesApi.services.postNotes(notes)
                if(result.isSuccessful){
                    Log.d("POST_NOTES", "Success: ${result.code()}")
                }
                else{
                    Log.d("POST_NOTES", "Failed: ${result.message()}")
                }
            }catch (e: Exception){
                Log.d("POST_NOTES", "Failed: ${e.message}")
            }
        }
    }
}