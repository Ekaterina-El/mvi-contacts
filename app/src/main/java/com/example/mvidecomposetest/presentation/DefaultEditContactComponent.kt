package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.Contact
import com.example.mvidecomposetest.domain.EditContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultEditContactComponent(
    private val componentContext: ComponentContext,
    private val contact: Contact
) : EditContactComponent, ComponentContext by componentContext {
    private val editContactUseCase by lazy { EditContactUseCase(RepositoryImpl) }

    private val _module = MutableStateFlow(
        stateKeeper.consume(KEY, EditContactComponent.Module::class) ?:
            EditContactComponent.Module(userName = contact.username, phone = contact.phone)
    )

    override val module: StateFlow<EditContactComponent.Module> get() = _module.asStateFlow()

    init {
        stateKeeper.register(KEY) { _module.value }
    }

    override fun onUserNameChanged(userName: String) {
        _module.value = _module.value.copy(userName = userName)
    }

    override fun onPhoneChanged(phone: String) {
        _module.value = _module.value.copy(phone = phone)
    }

    override fun onSaveClicked() {
        val (username, phone) = _module.value
        editContactUseCase(contact.copy(username = username, phone = phone))
    }

    companion object {
        private const val KEY = "DefaultEditContactComponent"
    }
}