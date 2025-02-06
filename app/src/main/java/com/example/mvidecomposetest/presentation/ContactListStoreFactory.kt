package com.example.mvidecomposetest.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.Contact
import com.example.mvidecomposetest.domain.GetContactsUseCase
import kotlinx.coroutines.launch

class ContactListStoreFactory{
    // TODO: in normal APP get as argument by DI
    private val storeFactory: StoreFactory = DefaultStoreFactory()
    private val getContactUseCase = GetContactsUseCase(RepositoryImpl)

    fun create(): ContactListStore = object : ContactListStore,
        Store<ContactListStore.Intent, ContactListStore.State, ContactListStore.Label> by storeFactory.create(
            name = "ContactListStore",
            initialState = ContactListStore.State(contacts = listOf()),
            reducer = ReducerImpl,
            executorFactory = ::ExecutorImpl,
            bootstrapper = BootstrapperImpl()
        ) {}

    private sealed interface Action {
        data class ContactsUpdated(val contacts: List<Contact>): Action
    }

    private sealed interface Message {
        data class ContactsUpdated(val contacts: List<Contact>): Message
    }

    private inner class BootstrapperImpl: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getContactUseCase().collect { contacts ->
                    dispatch(action = Action.ContactsUpdated(contacts = contacts))
                }
            }
        }
    }

    private object ReducerImpl: Reducer<ContactListStore.State, Message> {
        override fun ContactListStore.State.reduce(msg: Message) = when (msg) {
            is Message.ContactsUpdated -> copy(contacts = msg.contacts)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<ContactListStore.Intent, Action, ContactListStore.State, Message, ContactListStore.Label>() {

        override fun executeAction(action: Action, getState: () -> ContactListStore.State) {
            when (action) {
                is Action.ContactsUpdated -> dispatch(Message.ContactsUpdated(action.contacts))
            }
        }

        override fun executeIntent(
            intent: ContactListStore.Intent,
            getState: () -> ContactListStore.State,
        ) {
            when (intent) {
                ContactListStore.Intent.AddContact -> publish(ContactListStore.Label.AddContact)
                is ContactListStore.Intent.EditContact -> publish(
                    ContactListStore.Label.EditContact(
                        contact = intent.contact
                    )
                )
            }
        }
    }
}