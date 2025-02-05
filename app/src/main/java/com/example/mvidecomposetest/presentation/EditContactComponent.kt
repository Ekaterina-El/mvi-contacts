package com.example.mvidecomposetest.presentation

import kotlinx.coroutines.flow.StateFlow

interface EditContactComponent {
    val model: StateFlow<EditContactStore.State>

    fun onUserNameChanged(userName: String)
    fun onPhoneChanged(phone: String)
    fun onSaveClicked()
}