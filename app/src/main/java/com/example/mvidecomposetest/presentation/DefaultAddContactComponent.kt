package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposetest.core.componentScore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultAddContactComponent(
    componentContext: ComponentContext,
    private val onContactSaved: () -> Unit
) : AddContactComponent, ComponentContext by componentContext {
    private lateinit var store: AddContactStore

    init {
        componentScore().launch {
            store.labels.collect { label ->
                when (label) {
                    AddContactStore.Label.ContactSaved -> onContactSaved()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AddContactStore.State> get() = store.stateFlow

    override fun onUserNameChanged(userName: String) {
        store.accept(AddContactStore.Intent.ChangeUsername(userName))
    }

    override fun onPhoneChanged(phone: String) {
        store.accept(AddContactStore.Intent.ChangePhone(phone))
    }

    override fun onSaveContactClicked() {
        store.accept(AddContactStore.Intent.SaveContact)
    }
}