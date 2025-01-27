package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.AddContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultAddContactComponent(
    componentContext: ComponentContext
) : AddContactComponent, ComponentContext by componentContext {
    private val addContactUserCase by lazy { AddContactUseCase(RepositoryImpl) }

    private val _model = MutableStateFlow(
        stateKeeper.consume(KEY, AddContactComponent.Model::class) ?: AddContactComponent.Model(userName = "", phone = "")
    )

    override val model: StateFlow<AddContactComponent.Model> get() = _model.asStateFlow()

    init {
        stateKeeper.register(KEY) { _model.value }
    }

    override fun onUserNameChanged(userName: String) {
        _model.value = _model.value.copy(userName = userName)
    }

    override fun onPhoneChanged(phone: String) {
        _model.value = _model.value.copy(phone = phone)
    }

    override fun onSaveContactClicked() {
        _model.value.also { addContactUserCase(username = it.userName, phone = it.phone) }
    }

    companion object {
        private const val KEY = "DefaultAddContactComponent"
    }
}