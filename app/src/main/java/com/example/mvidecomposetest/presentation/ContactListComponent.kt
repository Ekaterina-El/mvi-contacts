package com.example.mvidecomposetest.presentation

import com.example.mvidecomposetest.domain.Contact
import kotlinx.coroutines.flow.StateFlow

interface ContactListComponent {
    val model: StateFlow<Model>

    data class Model(
        val contacts: List<Contact>
    )

    fun onAddContactClicked()
    fun onContactClicked(contact: Contact)
}