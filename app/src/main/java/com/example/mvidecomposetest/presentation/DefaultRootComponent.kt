package com.example.mvidecomposetest.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.example.mvidecomposetest.domain.Contact

class DefaultRootComponent(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    fun child(
        componentContext: ComponentContext,
        config: Config
    ): ComponentContext {
        return when (config) {
            Config.ContactList -> DefaultContactListComponent(
                componentContext = componentContext,
                onEditingContactRequested = { contact ->
                    navigation.push(
                        configuration = Config.EditContact(contact)
                    )
                },
                onAddContactRequested = {
                    navigation.push(
                        configuration = Config.AddContact
                    )
                }
            )

            Config.AddContact -> DefaultAddContactComponent(
                componentContext = componentContext,
                onContactSaved = navigation::pop
            )

            is Config.EditContact -> DefaultEditContactComponent(
                componentContext = componentContext,
                contact = config.contact,
                onContactSaved = navigation::pop
            )
        }
    }

    sealed interface Config: Parcelable {
        @Parcelize
        object ContactList: Config

        @Parcelize
        object AddContact: Config

        @Parcelize
        data class EditContact(val contact: Contact): Config
    }
}