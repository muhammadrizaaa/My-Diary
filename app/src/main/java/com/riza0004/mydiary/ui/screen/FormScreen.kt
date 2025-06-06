package com.riza0004.mydiary.ui.screen

import android.content.ContentResolver
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.riza0004.mydiary.R
import com.riza0004.mydiary.dataclass.Notes
import com.riza0004.mydiary.dataclass.User
import com.riza0004.mydiary.network.UserDataStore
import com.riza0004.mydiary.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navHostController: NavHostController = rememberNavController()){
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var nameIsErr by remember { mutableStateOf(false) }
    var contentIsErr by remember { mutableStateOf(false) }
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack()}) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            nameIsErr = name.isBlank()
                            contentIsErr = content.isBlank()
                            if(!nameIsErr && !contentIsErr){

                                viewModel.saveData(
                                    Notes(
                                        title = name,
                                        content = content,
                                        email = user.email,
                                        photo =  "",
                                        delPhoto = ""
                                    ),
                                    bitmap!!,
                                    onSuccess = {navHostController.popBackStack()},
                                    onFailed = {
                                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_check_24),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {innerPadding->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(bitmap == null){
                Image(
                    painter = painterResource(R.drawable.baseline_broken_image_24),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).border(border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary), shape = RectangleShape),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                )
            }
            else{
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).border(border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary), shape = RectangleShape)
                )
            }
            IconButton(
                onClick = {
                    val options = CropImageContractOptions(
                            null, CropImageOptions(
                            imageSourceIncludeGallery = false,
                            imageSourceIncludeCamera = true,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(options)
                },
                colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_a_photo_24),
                    contentDescription = stringResource(R.string.add_img_btn),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            OutlinedTextField(
                value = name,
                onValueChange = {name = it},
                label = { Text(stringResource(R.string.title_txt_field)) },
                isError = nameIsErr,
                supportingText = { FormIsErr(nameIsErr, stringResource(R.string.title_txt_field_err)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
            )
            OutlinedTextField(
                value = content,
                onValueChange = {content = it},
                label = { Text(stringResource(R.string.content_txt_field)) },
                isError = contentIsErr,
                supportingText = { FormIsErr(contentIsErr, stringResource(R.string.content_txt_field_err)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
        }
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap?{
    if(!result.isSuccessful){
        Log.e("IMG_CROPPER", "result: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null

    return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(resolver, uri)
    }
    else{
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@Composable
fun FormIsErr(isError: Boolean, msg: String){
    if(isError){
        Text(
            msg,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun FormScreenPrev() {
    FormScreen()
}