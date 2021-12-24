package ru.emkn.kotlin.sms.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.emkn.kotlin.sms.Group

//Окно, содержащее список групп, позволяет открывать окна групп @Composable @Preview
class GroupListController(val groups: List<Group>) {

    @Composable @Preview
    fun createWindow(){
        Window(
            onCloseRequest = {  },
            title = "huoybo",
        ) {
            content()
        }
    }
    @Composable @Preview
    fun content(){}

}