package com.example.lab07_exercise1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lab07_exercise1.data.ContactDatabaseHelper
import com.example.lab07_exercise1.provider.ContactProvider
import android.content.ContentValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Contacts") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank() && phoneNumber.isNotBlank()) {
                        val values = ContentValues().apply {
                            put(ContactDatabaseHelper.COLUMN_FIRST_NAME, firstName)
                            put(ContactDatabaseHelper.COLUMN_LAST_NAME, lastName)
                            put(ContactDatabaseHelper.COLUMN_PHONE_NUMBER, phoneNumber)
                        }
                        context.contentResolver.insert(ContactProvider.CONTENT_URI, values)
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && phoneNumber.isNotBlank()
            ) {
                Text("SAVE", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

