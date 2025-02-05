package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposetest.core.componentScore
import com.example.mvidecomposetest.domain.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultEditContactComponent(
    private val componentContext: ComponentContext,
    private val contact: Contact,
    private val onContactSaved: () -> Unit
) : EditContactComponent, ComponentContext by componentContext {
    private lateinit var store: EditContactStore

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<EditContactStore.State> get() = store.stateFlow

    init {
        componentScore().launch {
            store.labels.collect { label ->
                when (label) {
                    EditContactStore.Label.ContactSaved -> onContactSaved
                }
            }
        }
    }

    override fun onUserNameChanged(userName: String) {
        store.accept(EditContactStore.Intent.ChangeUserName(userName))
    }

    override fun onPhoneChanged(phone: String) {
        store.accept(EditContactStore.Intent.ChangePhone(phone))
    }

    override fun onSaveClicked() {
        store.accept(EditContactStore.Intent.SaveContact)
        onContactSaved()
    }
}