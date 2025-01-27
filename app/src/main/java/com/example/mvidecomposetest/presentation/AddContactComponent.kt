package com.example.mvidecomposetest.presentation

import android.os.Parcelable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

interface AddContactComponent {

    val model: StateFlow<Model>

    @Parcelize
    data class Model(
        val userName: String,
        val phone: String
    ) : Parcelable

    fun onUserNameChanged(userName: String)
    fun onPhoneChanged(phone: String)
    fun onSaveContactClicked()
}