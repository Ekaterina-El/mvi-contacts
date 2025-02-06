package com.example.mvidecomposetest.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.AddContactUseCase

class AddContactStoreFactory {
    // TODO: in normal APP get as argument by DI
    private val storeFactory: StoreFactory = DefaultStoreFactory()
    private val addContactUseCase = AddContactUseCase(RepositoryImpl)

    fun create(): AddContactStore = object : AddContactStore,
        Store<AddContactStore.Intent, AddContactStore.State, AddContactStore.Label> by storeFactory.create(
            name = "AddContactStore",
            initialState = AddContactStore.State(username = "", phone = ""),
            reducer = ReducerImpl,
            executorFactory = ::ExecutorImpl
        ) {}

    private sealed interface Action

    private sealed interface Message {
        data class ChangeUserName(val username: String): Message
        data class ChangePhone(val phone: String): Message
    }

    private object ReducerImpl: Reducer<AddContactStore.State, Message> {
        override fun AddContactStore.State.reduce(msg: Message): AddContactStore.State = when (msg) {
            is Message.ChangePhone -> copy(phone = msg.phone)
            is Message.ChangeUserName -> copy(username = msg.username)
        }
    }

    private inner class ExecutorImpl:
        CoroutineExecutor<AddContactStore.Intent, Action, AddContactStore.State, Message, AddContactStore.Label>() {
        override fun executeIntent(
            intent: AddContactStore.Intent,
            getState: () -> AddContactStore.State,
        ) {
            when (intent) {
                is AddContactStore.Intent.ChangePhone -> dispatch(Message.ChangePhone(phone = intent.phone))
                is AddContactStore.Intent.ChangeUsername -> dispatch(Message.ChangeUserName(username = intent.username))
                AddContactStore.Intent.SaveContact -> {
                    val state = getState()
                    addContactUseCase(username = state.username, phone = state.phone)
                    publish(AddContactStore.Label.ContactSaved)
                }
            }
        }
    }
}