package com.example.mvidecomposetest.presentation

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.mvidecomposetest.domain.Contact
import kotlinx.parcelize.Parcelize

class DefaultRootComponent(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<Config, RootComponent.Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.ContactList,
            handleBackButton = true,
            childFactory = ::child
        )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            Config.AddContact -> RootComponent.Child.AddContact(DefaultAddContactComponent(
                componentContext = componentContext,
                onContactSaved = navigation::pop
            ))

            is Config.EditContact -> RootComponent.Child.EditContact(
                DefaultEditContactComponent(
                    componentContext = componentContext,
                    contact = config.contact,
                    onContactSaved = navigation::pop
                )
            )

            Config.ContactList -> RootComponent.Child.ContactsList(
                DefaultContactListComponent(
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