package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun MainContent(
    modifier: Modifier = Modifier,
    items: List<Item>,
    inputText: String,
    onItemClicked: (id: Long) -> Unit,
    onItemDeleteClicked: (id: Long) -> Unit,
    onAddItemClicked: () -> Unit,
    onInputTextChanged: (String) -> Unit,
) {
    Column(modifier) {
        Box(Modifier.weight(1F)) {
            ListContent(
                items = items,
                onItemClicked = onItemClicked,
                onItemDeleteClicked = onItemDeleteClicked,
            )
        }

        Input(
            text = inputText,
            onAddClicked = onAddItemClicked,
            onTextChanged = onInputTextChanged
        )
    }
}


@Composable
private fun ListContent(
    items: List<Item>,
    onItemClicked: (id: Long) -> Unit,
    onItemDeleteClicked: (id: Long) -> Unit,
) {
    Box {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(items) { item ->
                Item(
                    item = item,
                    onClicked = { onItemClicked(item.id) },
                    onDeleteClicked = { onItemDeleteClicked(item.id) }
                )

                Divider()
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = listState)
        )
    }
}

@Composable
private fun Item(
    item: Item,
    onClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(modifier = Modifier.clickable(onClick = onClicked)) {
        Spacer(modifier = Modifier.width(8.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = AnnotatedString( item.text.split(",").joinToString(" ") ),
            modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onDeleteClicked) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Input(
    text: String,
    onTextChanged: (String) -> Unit,
    onAddClicked: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = text,
            modifier = Modifier
                .weight(weight = 1F)
                .onKeyUp(key = Key.Enter, action = onAddClicked),
            onValueChange = onTextChanged,
            label = { Text(text = "Add row") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onAddClicked) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}
