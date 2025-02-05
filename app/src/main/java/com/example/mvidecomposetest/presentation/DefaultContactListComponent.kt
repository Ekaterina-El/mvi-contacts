package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposetest.core.componentScore
import com.example.mvidecomposetest.domain.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultContactListComponent(
    componentContext: ComponentContext,
    private val onEditingContactRequested: (contact: Contact) -> Unit,
    private val onAddContactRequested: () -> Unit
) : ContactListComponent, ComponentContext by componentContext {
    private lateinit var store: ContactListStore

    init {
        componentScore().launch {
            store.labels.collect { label ->
                when (label) {
                    ContactListStore.Label.AddContact -> onAddContactRequested()
                    is ContactListStore.Label.EditContact -> onEditingContactRequested(label.contact)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<ContactListStore.State> get() = store.stateFlow

    override fun onAddContactClicked() = store.accept(ContactListStore.Intent.AddContact)
    override fun onContactClicked(contact: Contact) = store.accept(ContactListStore.Intent.EditContact(contact))
}