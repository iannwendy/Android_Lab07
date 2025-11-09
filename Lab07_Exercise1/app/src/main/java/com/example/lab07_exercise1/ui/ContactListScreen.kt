package com.example.lab07_exercise1.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lab07_exercise1.data.Contact
import com.example.lab07_exercise1.data.ContactDatabaseHelper
import com.example.lab07_exercise1.provider.ContactProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    onAddContactClick: () -> Unit,
    activity: ComponentActivity,
    reloadKey: Int = 0
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var phoneNumberToCall by remember { mutableStateOf<String?>(null) }

    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && phoneNumberToCall != null) {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${phoneNumberToCall}")
            }
            context.startActivity(intent)
        }
        phoneNumberToCall = null
    }

    // Load contacts from ContentProvider
    LaunchedEffect(searchQuery, reloadKey) {
        val cursor = context.contentResolver.query(
            ContactProvider.CONTENT_URI,
            null,
            if (searchQuery.isBlank()) null
            else "${ContactDatabaseHelper.COLUMN_FIRST_NAME} LIKE ? OR ${ContactDatabaseHelper.COLUMN_LAST_NAME} LIKE ? OR ${ContactDatabaseHelper.COLUMN_PHONE_NUMBER} LIKE ?",
            if (searchQuery.isBlank()) null
            else arrayOf("%$searchQuery%", "%$searchQuery%", "%$searchQuery%"),
            "${ContactDatabaseHelper.COLUMN_FIRST_NAME} ASC, ${ContactDatabaseHelper.COLUMN_LAST_NAME} ASC"
        )

        val contactList = mutableListOf<Contact>()
        cursor?.use {
            val idIndex = it.getColumnIndex(ContactDatabaseHelper.COLUMN_ID)
            val firstNameIndex = it.getColumnIndex(ContactDatabaseHelper.COLUMN_FIRST_NAME)
            val lastNameIndex = it.getColumnIndex(ContactDatabaseHelper.COLUMN_LAST_NAME)
            val phoneIndex = it.getColumnIndex(ContactDatabaseHelper.COLUMN_PHONE_NUMBER)

            while (it.moveToNext()) {
                contactList.add(
                    Contact(
                        id = it.getLong(idIndex),
                        firstName = it.getString(firstNameIndex),
                        lastName = it.getString(lastNameIndex),
                        phoneNumber = it.getString(phoneIndex)
                    )
                )
            }
        }
        contacts = contactList
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Contacts") },
                actions = {
                    IconButton(onClick = onAddContactClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Contact")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            // Contact list
            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No contacts found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(contacts) { contact ->
                        ContactItem(
                            contact = contact,
                            onCallClick = {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CALL_PHONE
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        val intent = Intent(Intent.ACTION_CALL).apply {
                                            data = Uri.parse("tel:${contact.phoneNumber}")
                                        }
                                        context.startActivity(intent)
                                    }
                                    else -> {
                                        phoneNumberToCall = contact.phoneNumber
                                        callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                                    }
                                }
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCallClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = contact.fullName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal
        )
    }
}

