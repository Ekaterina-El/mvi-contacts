
* В MVI всё управляется Intent-ами. 
* Intent-ы обрабатываются в Store
* У store только один вход (публичная функция) и только один выход (публичная переменная).

  
![Общая схема](https://github.com/Ekaterina-El/mvi-contacts/blob/master/image1.png)

![Общение между компанентами MVI](https://github.com/Ekaterina-El/mvi-contacts/blob/master/image2.png)

![](https://github.com/Ekaterina-El/mvi-contacts/blob/master/image3.png)

```kotlin
implementation("com.arkivanov.mvikotlin:mvikotlin:3.2.1")  
implementation("com.arkivanov.mvikotlin:mvikotlin-main:3.2.1")  
implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:3.2.1")
```

Store для добавления контакта. В Label описываются какие-то одноразовые действия
Если Intent для общения из вне со Store, то Label для общения из Store с "вне"
```kotlin
interface AddContactStore: Store<AddContactStore.Intent, AddContactStore.State, AddContactStore.Label> {  
    data class State(  
        val username: String,  
        val phone: String  
    )  
  
    sealed interface Label {  
        object ContactSaved: Label  
    }  
  
    sealed interface Intent {  
        data class ChangeUsername(val username: String): Intent  
        data class ChangePhone(val phone: String): Intent  
        object SaveContact: Intent  
    }  
}
```

Пример использования Store
```kotlin
class DefaultAddContactComponent(  
    componentContext: ComponentContext,  
    private val onContactSaved: () -> Unit  
) : AddContactComponent, ComponentContext by componentContext {  
    private lateinit var store: AddContactStore  
  
    init {  
        componentScore().launch {  
            store.labels.collect {  
                when (it) {  
                    AddContactStore.Label.ContactSaved -> onContactSaved()  
                }  
            }  
        }    }  
  
    @OptIn(ExperimentalCoroutinesApi::class)  
    override val model: StateFlow<AddContactStore.State> get() = store.stateFlow  
  
    override fun onUserNameChanged(userName: String) {  
        store.accept(AddContactStore.Intent.ChangeUsername(userName))  
    }  
  
    override fun onPhoneChanged(phone: String) {  
        store.accept(AddContactStore.Intent.ChangePhone(phone))  
    }  
  
    override fun onSaveContactClicked() {  
        store.accept(AddContactStore.Intent.SaveContact)  
    }  
}
```


Для создания Store нужно создать AddContactStoreFactory
Чтобы не создавать в ручную можно использовать StoreFactory.create

Где Message - это внутренняя сущность которая должна вызываться если, например, обновилось состояние в репозитории (не намерение от пользователя, за это отвечает Intent). Message формируется от поступающих Action-ов и Inten-ов

Есть несколько реализаций Executor-ов. Т.к мы используем коруниты, то и используем CoroutineExecutor

Т.к мы не используем Action, а используем только Intent, то значит оверайдим только excecureIntent, но не используем executeAction

Важно помнить что только Reducer может быть Singeltrone. Executor и Bootstrapper - хранят состояние.

```kotlin
class AddContactStoreFactory(private val storeFactory: StoreFactory) {  

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
```


Для действий которые требуется сделать при инициализации стора можно использовать Bootstrapper.

Например, тут мы подписываемся на изменение и при загрузке новых контактов отправляем Action

Этот Action обрабатывается в executor и затем приходит в  виде Message в reducer

```kotlin
private inner class BootstrapperImpl: CoroutineBootstrapper<Action>() {  
    override fun invoke() {  
        scope.launch {  
            getContactUseCase().collect { contacts ->  
                dispatch(action = Action.ContactsUpdated(contacts = contacts))  
            }  
        }    }  
}

private sealed interface Action {  
    data class ContactsUpdated(val contacts: List<Contact>): Action  
}

private sealed interface Message {  
    data class ContactsUpdated(val contacts: List<Contact>): Message  
}

private object ReducerImpl: Reducer<ContactListStore.State, Message> {  
    override fun ContactListStore.State.reduce(msg: Message) = when (msg) {  
        is Message.ContactsUpdated -> copy(contacts = msg.contacts)  
    }  
}

private inner class ExecutorImpl: CoroutineExecutor<ContactListStore.Intent, Action, ContactListStore.State, Message, ContactListStore.Label>() {
	...
	override fun executeAction(action: Action, getState: () -> ContactListStore.State) {  
	    when (action) {  
	        is Action.ContactsUpdated -> dispatch(Message.ContactsUpdated(action.contacts))  
	    }  
	}
}
```

Чтобы создать store можно использовать instanceKeeper.getOrCreate чтобы при перевороте экрана store не пересоздавался. Но в таком случае для Store нужно будет реализовать интерфейс InstanceKeeper.Instance

```kotlin
private val store = instanceKeeper.getOrCreate {  
    EditContactStoreFactory().create(contact)  
}
```

Но лучше использовать instanceKeeper.getStore (самостоятельно создает обертку)

```kotlin
private val store = instanceKeeper.getStore {  
    EditContactStoreFactory().create(contact)  
}
```



Для облегчения работы можно использовать Templates для создание сущностей MVI
https://github.com/arkivanov/MVIKotlin/blob/master/docs/assets/live-templates.zip


Для добавления логирования:

```kotlin
implementation("com.arkivanov.mvikotlin:mvikotlin-logging:3.2.1")
```

```kotlin
private val storeFactory: StoreFactory = LoggingStoreFactory(DefaultStoreFactory())
```
