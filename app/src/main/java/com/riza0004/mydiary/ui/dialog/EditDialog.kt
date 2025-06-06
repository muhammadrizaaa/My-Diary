package com.riza0004.mydiary.ui.dialog

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.riza0004.mydiary.R
import com.riza0004.mydiary.dataclass.Notes
import com.riza0004.mydiary.ui.theme.MyDiaryTheme

@Composable
fun EditDialog(
    note: Notes,
    onDismissReq: () -> Unit,
    onConfirm: (String, String) -> Unit
){
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    Dialog(onDismissReq) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit),
                    style = MaterialTheme.typography.titleLarge
                )
                AsyncImage(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.baseline_broken_image_24),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(note.photo)
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
                OutlinedTextField(
                    value = title,
                    onValueChange = {title = it},
                    label = { Text(stringResource(R.string.title_txt_field)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = {content = it},
                    label = { Text(stringResource(R.string.content_txt_field)) },
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = onDismissReq,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                    OutlinedButton(
                        onClick = {
                            onConfirm(title, content)
                        },
                        enabled = (title.isNotEmpty()&&content.isNotEmpty()),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DialogHewanPrev() {
    MyDiaryTheme {
        EditDialog(
            onDismissReq = {},
            onConfirm = {_, _ ->},
            note = Notes(
                title = "title",
                content = "content",
                email = "emm@email.co",
                photo = "",
                delPhoto = ""
            )
        )
    }
}