package com.example.mvidecomposetest.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.example.mvidecomposetest.presentation.RootComponent
import com.example.mvidecomposetest.ui.theme.MviDecomposeTestTheme

@Composable
fun RootComponentContent(component: RootComponent) {
    MviDecomposeTestTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Children(stack = component.childStack) {
                when (val instance = it.instance) {
                    is RootComponent.Child.AddContact -> AddContactContent(instance.component)
                    is RootComponent.Child.ContactsList -> ContactListContent(instance.component)
                    is RootComponent.Child.EditContact -> EditContactContent(instance.component)
                }
            }
        }
    }
}