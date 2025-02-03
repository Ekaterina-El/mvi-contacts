package com.example.mvidecomposetest.presentation

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

interface EditContactComponent {
    val model: StateFlow<Model>

    @Parcelize
    data class Model(
        val userName: String,
        val phone: String
    ): Parcelable

    fun onUserNameChanged(userName: String)
    fun onPhoneChanged(phone: String)
    fun onSaveClicked()
}