package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class RootStore {

    var state: RootState by mutableStateOf(initialState())
        private set

    fun onItemClicked(id: Long) {
        setState { copy(editingItemId = id) }
    }

    fun onItemDeleteClicked(id: Long) {
        setState { copy(items = items.filterNot { it.id == id }) }
    }

    fun onAddItemClicked() {
        setState {
            val newItem =
                Item(
                    id = items.maxOfOrNull(Item::id)?.plus(1L) ?: 1L,
                    text = inputText,
                )

            copy(items = items + newItem, inputText = "")
        }
    }

    fun onInputTextChanged(text: String) {
        setState { copy(inputText = text) }
    }

    fun onEditorCloseClicked() {
        setState { copy(editingItemId = null) }
    }

    fun onEditorTextChanged(text: String) {
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(text = text) }
        }
    }

    fun onSortClicked() {
        setState {
            copy(items = items.sortedBy { it.text })
        }
    }

    private fun RootState.updateItem(id: Long, transformer: (Item) -> Item): RootState =
        copy(items = items.updateItem(id = id, transformer = transformer))

    private fun List<Item>.updateItem(id: Long, transformer: (Item) -> Item): List<Item> =
        map { item -> if (item.id == id) transformer(item) else item }

    private fun initialState(): RootState =
        RootState(
            items = listOf()
        )

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }

    data class RootState(
        val items: List<Item> = emptyList(),
        val inputText: String = "",
        val editingItemId: Long? = null,
    )
}
