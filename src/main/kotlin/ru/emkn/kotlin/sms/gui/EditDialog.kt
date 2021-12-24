package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun EditDialog(
    item: Item,
    onCloseClicked: () -> Unit,
    onTextChanged: (String) -> Unit,
) {
    Dialog(
        title = "Edit",
        onCloseRequest = onCloseClicked,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = item.text,
                modifier = Modifier.weight(1F).fillMaxWidth().sizeIn(minHeight = 192.dp),
                label = { Text("Text") },
                onValueChange = onTextChanged,
            )
        }
    }
}
