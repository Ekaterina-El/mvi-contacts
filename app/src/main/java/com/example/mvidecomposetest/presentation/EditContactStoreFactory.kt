package com.example.mvidecomposetest.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.mvidecomposetest.domain.Contact
import com.example.mvidecomposetest.domain.EditContactUseCase

class EditContactStoreFactory(
    private val storeFactory: StoreFactory,
    private val editContactUseCase: EditContactUseCase
) {
    fun create(contact: Contact): EditContactStore = object : EditContactStore,
        Store<EditContactStore.Intent, EditContactStore.State, EditContactStore.Label> by storeFactory.create(
            name = "EditContactStore",
            initialState = EditContactStore.State(id = contact.id, username = contact.username, phone = contact.phone),
            reducer = ReducerImpl,
            executorFactory = ::ExecutorImpl
        ) {}

    sealed interface Action

    sealed interface Message {
        data class ChangeUserName(val username: String): Message
        data class ChangePhone(val phone: String): Message
    }

    object ReducerImpl: Reducer<EditContactStore.State, Message> {
        override fun EditContactStore.State.reduce(msg: Message): EditContactStore.State = when (msg) {
            is Message.ChangePhone -> copy(phone = msg.phone)
            is Message.ChangeUserName -> copy(username = msg.username)
        }
    }

    private inner class ExecutorImpl:
            CoroutineExecutor<EditContactStore.Intent, Action, EditContactStore.State, Message, EditContactStore.Label>() {
        override fun executeIntent(
            intent: EditContactStore.Intent,
            getState: () -> EditContactStore.State,
        ) {
            when (intent) {
                is EditContactStore.Intent.ChangePhone -> dispatch(Message.ChangePhone(phone = intent.phone))
                is EditContactStore.Intent.ChangeUserName -> dispatch(Message.ChangeUserName(username = intent.username))
                EditContactStore.Intent.SaveContact -> {
                    getState().also {
                        editContactUseCase(Contact(
                            id = it.id,
                            username = it.username,
                            phone = it.phone
                        ))
                        publish(EditContactStore.Label.ContactSaved)
                    }
                }
            }
        }
    }
}