package com.riza0004.mydiary.ui.screen

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.riza0004.mydiary.BuildConfig
import com.riza0004.mydiary.R
import com.riza0004.mydiary.dataclass.Notes
import com.riza0004.mydiary.dataclass.User
import com.riza0004.mydiary.network.ApiStatus
import com.riza0004.mydiary.network.UserDataStore
import com.riza0004.mydiary.ui.dialog.DeleteDialog
import com.riza0004.mydiary.ui.dialog.EditDialog
import com.riza0004.mydiary.ui.dialog.ProfileDialog
import com.riza0004.mydiary.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navHostController: NavHostController = rememberNavController()){
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())
    var showProfileDialog by remember { mutableStateOf(false) }
    var goToForm by remember { mutableStateOf(false) }
    val viewModel:MainViewModel = viewModel()
    val status = viewModel.status.collectAsState()
    LaunchedEffect(user.email) {
        if(user.email.isNotEmpty() && goToForm){
            navHostController.navigate("formScreen")
            goToForm = false
        }
        if(user.email.isNotEmpty()){
            viewModel.retrieveData(user.email)
        }
        else{
            viewModel.retrieveData("-")
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    if(user.email.isEmpty()) {
                        TextButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    signIn(context, dataStore)
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.sign_in),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                            )
                        }
                    }
                    else{
                        IconButton(
                            onClick = {
                                showProfileDialog = true
                            },
                            modifier = Modifier.wrapContentSize()
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.photo)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.loading_img),
                                error = painterResource(R.drawable.baseline_broken_image_24),
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                        shape = CircleShape
                                    )
                            )

                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if(user.email.isEmpty()){
                    CoroutineScope(Dispatchers.IO).launch {
                        signIn(context, dataStore)
                        goToForm = true
                    }
                }
                else{
                    navHostController.navigate("formScreen")
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_24),
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
    ) {innerPadding->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when(status.value){
                ApiStatus.SUCCESS-> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 84.dp)
                    ) {
                        items(viewModel.data.value){
                            ListItem(
                                it,
                                viewModel,
                                idUser = user.email
                            )
                        }
                    }
                }
                ApiStatus.FAILED -> {
                    Text(
                        text = stringResource(R.string.failed_json)
                    )
                    Button(
                        onClick = { viewModel.retrieveData(user.email) }
                    ) {
                        Text(
                            text = stringResource(R.string.try_again)
                        )
                    }
                }
                ApiStatus.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                ApiStatus.EMPTY->{
                    Text(stringResource(R.string.empty_list))
                }
            }
        }
        if(showProfileDialog){
            ProfileDialog(
                userDataClass = user,
                onDismissReq = { showProfileDialog = false },
                onConfirm = {
                    showProfileDialog = false
                    CoroutineScope(Dispatchers.IO).launch {
                        signOut(context, dataStore)
                    }
                }
            )
        }
    }
}

@Composable
fun ListItem(
    notes: Notes,
    viewModel: MainViewModel,
    idUser: String
){
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    if(showDeleteDialog){
        DeleteDialog(
            note = notes,
            onConfirm = {
                viewModel.deleteData(notes.id, idUser = idUser)
                showDeleteDialog = false
            },
            onDismissReq = {showDeleteDialog = false}
        )
    }
    if(showEditDialog){
        EditDialog(
            note = notes,
            onConfirm = {title, content ->
                val noteToEdit = notes.copy(
                    title = title,
                    content = content
                )
                viewModel.editData(noteToEdit)
                showEditDialog = false
            },
            onDismissReq = {
                showEditDialog = false
            }
        )
    }
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(60.dp)
                    .border(border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
            ){
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.baseline_broken_image_24),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(notes.photo)
                        .crossfade(true)
                        .listener(
                            onError = { request, throwable ->
                                Log.e("COIL", "Image load failed ${request.error} ", throwable.throwable)
                            },
                            onSuccess = { request, result ->
                                Log.d("COIL", "Image loaded successfully ${request.data} ${result.request}")
                            }
                        )
                        .build(),
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = notes.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notes.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = {showEditDialog = true}) {
                Icon(
                    painterResource(R.drawable.baseline_edit_24),
                    contentDescription = stringResource(R.string.edit)
                )
            }
            IconButton(onClick = {showDeleteDialog = true}) {
                Icon(
                    painterResource(R.drawable.baseline_delete_24),
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
        HorizontalDivider()
    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore){
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException){
        Log.e("ERR_LOGIN", "Err: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
){
    val credential = result.credential
    if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d("LOGIN", "email: ${googleId.id}")
            val name = googleId.displayName?:""
            val email = googleId.id
            val photo = googleId.profilePictureUri.toString()
            dataStore.saveData(User(name, email, photo))
        }catch (e:GoogleIdTokenParsingException){
            Log.e("ERR_LOGIN", "Err: ${e.message}")
        }
    }
    else{
        Log.e("ERR_LOGIN", "ERR: unrecognized custom credential type")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore){
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException){
        Log.e("ERR_LOGOUT", "Err: ${e.errorMessage}")
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPrev() {
    MainScreen()
}