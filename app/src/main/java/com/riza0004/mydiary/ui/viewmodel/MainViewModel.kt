package com.riza0004.mydiary.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riza0004.mydiary.dataclass.Notes
import com.riza0004.mydiary.network.ApiStatus
import com.riza0004.mydiary.network.ImgbbApi
import com.riza0004.mydiary.network.NotesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

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
                if(data.value.isNotEmpty()){
                    status.value = ApiStatus.SUCCESS
                }
                else if(e.message == "HTTP 404 Not Found"){
                    status.value = ApiStatus.EMPTY
                }
                else{
                    status.value = ApiStatus.FAILED
                }
            }
        }
    }

    fun saveData(
        notes: Notes,
        imageFile: Bitmap,
        onSuccess: () -> Unit,
        onFailed: (String) -> Unit
    ){
        viewModelScope.launch {
            try {
                val resultImg = ImgbbApi.services.uploadImage(
                    image = imageFile.toMultipartBody()
                )

                if (resultImg.success) {
                    Log.d("IMG_POST", "Success: ${resultImg.data.url}")

                    val updatedNotes = notes.copy(photo = resultImg.data.url, delPhoto = resultImg.data.delete_url)

                    val result = NotesApi.services.postNotes(updatedNotes)

                    if (result.isSuccessful) {
                        Log.d("POST_NOTES", "Success: ${result.code()}")
                        onSuccess()
                    } else {
                        Log.d("POST_NOTES", "Failed: ${result.message()}")
                        onFailed(result.message())
                    }
                } else {
                    Log.d("IMG_POST", "Image upload failed: ${resultImg.status}")
                    onFailed("${resultImg.status}")
                }
            }catch (e: Exception) {
                Log.d("POST_NOTES", "Exception: ${e.message}")
                onFailed("${e.message}")
            }
        }
    }

    fun deleteData(
        id: String,
        idUser: String
    ){
        viewModelScope.launch {
            try {
                val result = NotesApi.services.deleteNotes(id)
                if(result.isSuccessful){
                    Log.d("DELETE_NOTES", "Success: ${result.code()}")
                    retrieveData(idUser)
                }
                else{
                    Log.d("DELETE_NOTES", "Failed: ${result.message()}")
                }
            } catch (e: Exception){
                Log.d("DELETE_NOTES", "Exception: ${e.message}")
            }
        }
    }

    fun editData(
        notes: Notes
    ){
        viewModelScope.launch {
            try {
                val result = NotesApi.services.editNotes(
                    id = notes.id,
                    note = notes
                )
                if(result.isSuccessful){
                    Log.d("EDIT_NOTES", "Success: ${result.code()}")
                    retrieveData(notes.email)
                }
                else{
                    Log.d("EDIT_NOTES", "Failed: ${result.message()}")
                }
            }
            catch (e: Exception){
                Log.d("EDIT_NOTES", "Exception: ${e.message}")
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part{
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody
        )
    }

}