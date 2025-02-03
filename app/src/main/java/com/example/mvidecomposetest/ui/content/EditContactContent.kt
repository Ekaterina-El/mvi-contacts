package com.example.mvidecomposetest.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mvidecomposetest.presentation.EditContactComponent


// TODO: this`s copy-past AddContact for practice
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactContent(
    component: EditContactComponent
) {
    val model by component.model.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = model.userName,
            placeholder = { Text(text = "Username:") },
            onValueChange = component::onUserNameChanged
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = model.phone,
            placeholder = { Text(text = "Phone:") },
            onValueChange = component::onPhoneChanged
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = component::onSaveClicked
        ) {
            Text(text = "Save")
        }
    }
}