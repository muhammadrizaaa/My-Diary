package com.riza0004.mydiary.ui.dialog

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.riza0004.mydiary.R
import com.riza0004.mydiary.dataclass.Notes
import com.riza0004.mydiary.ui.theme.MyDiaryTheme

@Composable
fun DeleteDialog(
    note: Notes,
    onDismissReq: () -> Unit,
    onConfirm: () -> Unit
){
    Dialog(
        onDismissRequest = onDismissReq
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(75.dp)
                )
                Text(
                    text = "Delete ${note.title}?",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = {onDismissReq()},
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                    OutlinedButton(
                        onClick = {onConfirm()},
                        modifier = Modifier.padding(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DeleteDialogPreview() {
    MyDiaryTheme {
        DeleteDialog(
            note = Notes(title = "Tes", content = "", photo = "123", email = "", delPhoto = ""),
            onDismissReq = {},
            onConfirm = {}
        )
    }
}