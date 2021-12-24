package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun RootContent(modifier: Modifier = Modifier) {
    val model = remember { RootStore() }
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
