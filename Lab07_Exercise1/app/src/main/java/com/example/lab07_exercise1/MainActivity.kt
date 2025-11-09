package com.example.lab07_exercise1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.lab07_exercise1.ui.AddContactScreen
import com.example.lab07_exercise1.ui.ContactListScreen
import com.example.lab07_exercise1.ui.theme.Lab07_Exercise1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab07_Exercise1Theme {
                ContactManagementApp(activity = this)
            }
        }
    }
}

@Composable
fun ContactManagementApp(activity: ComponentActivity) {
    var showAddContact by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableStateOf(0) }

    if (showAddContact) {
        AddContactScreen(
            onBackClick = {
                showAddContact = false
                reloadKey++
            }
        )
    } else {
        ContactListScreen(
            onAddContactClick = { showAddContact = true },
            activity = activity,
            reloadKey = reloadKey
        )
    }
}