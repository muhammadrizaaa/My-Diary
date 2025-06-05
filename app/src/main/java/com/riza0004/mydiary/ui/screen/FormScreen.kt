package com.riza0004.mydiary.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.riza0004.mydiary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navHostController: NavHostController = rememberNavController()){
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var nameIsErr by remember { mutableStateOf(false) }
    var contentIsErr by remember { mutableStateOf(false) }
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
                                navHostController.popBackStack()
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
            OutlinedTextField(
                value = name,
                onValueChange = {name = it},
                label = { Text(stringResource(R.string.title_txt_field)) },
                isError = nameIsErr,
                supportingText = { FormIsErr(nameIsErr, stringResource(R.string.title_txt_field_err)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content,
                onValueChange = {content = it},
                label = { Text(stringResource(R.string.content_txt_field)) },
                isError = contentIsErr,
                supportingText = { FormIsErr(contentIsErr, stringResource(R.string.content_txt_field_err)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
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