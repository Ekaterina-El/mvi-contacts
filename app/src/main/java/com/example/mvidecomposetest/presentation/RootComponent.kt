package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        class AddContact(val component: AddContactComponent): Child
        class EditContact(val component: EditContactComponent): Child
        class ContactsList(val component: ContactListComponent): Child
    }
}