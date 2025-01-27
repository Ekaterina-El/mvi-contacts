package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.example.mvidecomposetest.core.componentScore
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.Contact
import com.example.mvidecomposetest.domain.GetContactsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultContactListComponent(
    componentContext: ComponentContext,
    private val onEditingContactRequested: (contact: Contact) -> Unit,
    private val onAddContactRequested: () -> Unit
) : ContactListComponent, ComponentContext by componentContext {
    private val repository by lazy { RepositoryImpl }
    private val getContactsUseCase by lazy { GetContactsUseCase(repository) }

    private val coroutineScope = componentScore()

    override val module: StateFlow<ContactListComponent.Module> get() = getContactsUseCase()
        .map { ContactListComponent.Module(contacts = it) }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = ContactListComponent.Module(listOf())
        )

    override fun onAddContactClicked() = onAddContactRequested()
    override fun onContactClicked(contact: Contact) = onEditingContactRequested(contact)
}