package ru.emkn.kotlin.sms.gui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun RootContent(modifier: Modifier = Modifier) {
    val models = listOf(RootStore(), RootStore(), RootStore())
    val currentModel = remember { mutableStateOf(models[0]) }

    val str = remember { mutableStateOf("Список 0") }

    Row {
        Column {
            Button(onClick = { currentModel.value = models[0] ; str.value = "Список 0" }) { Text(text="0") }
            Button(onClick = { currentModel.value = models[1] ; str.value = "Список 1" }) { Text(text="1") }
            Button(onClick = { currentModel.value = models[2] ; str.value = "Список 2" }) { Text(text="2") }
        }
        Column {
            TopAppBar {
                Text(text = str.value)
            }
            view(modifier, currentModel)
        }
    }
}

@Composable
fun view(modifier: Modifier, currentModel: MutableState<RootStore>) {
    val model = currentModel.value
    val state = model.state
    MainContent(
        modifier = modifier,
        items = state.items,
        inputText = state.inputText,
        onItemClicked = model::onItemClicked,
        onItemDeleteClicked = model::onItemDeleteClicked,
        onAddItemClicked = model::onAddItemClicked,
        onInputTextChanged = model::onInputTextChanged,
        onSortClicked = model::onSortClicked,
    )

    state.editingItem?.also { item ->
        EditDialog(
            item = item,
            onCloseClicked = model::onEditorCloseClicked,
            onTextChanged = model::onEditorTextChanged,
        )
    }
}

private val RootStore.RootState.editingItem: Item?
    get() = editingItemId?.let(items::firstById)

private fun List<Item>.firstById(id: Long): Item =
    first { it.id == id }