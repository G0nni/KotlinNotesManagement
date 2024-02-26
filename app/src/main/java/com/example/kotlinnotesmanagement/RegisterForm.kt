package com.example.kotlinnotesmanagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper
import com.example.kotlinnotesmanagement.data.model.User

class RegisterForm : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                RegisterScreen(this)
            }
        }
    }

    // Fonction pour afficher un toast
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun RegisterScreen(activity: ComponentActivity) {
    val focusManager = LocalFocusManager.current
    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = setUsername,
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions { focusManager.clearFocus() },
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = setPassword,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions { focusManager.clearFocus() },
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                val dbHelper = DatabaseHelper(activity)
                val user = User(username = username, password = password)
                val userId = dbHelper.addUser(user) // Assurez-vous que cette méthode renvoie l'ID de l'utilisateur nouvellement créé
                if (userId != -1L) {
                    // Si l'utilisateur a été créé avec succès, stocker son ID dans les SharedPreferences
                    val sharedPreferences = activity.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("loggedInUserId", userId.toInt())
                    editor.apply()

                    // Afficher un message de succès et rediriger vers l'écran principal
                    showToast(activity, "User registered successfully")
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                } else {
                    // Si l'utilisateur n'a pas été créé avec succès, afficher un message d'erreur
                    showToast(activity, "Registration failed")
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Register")
        }

        Button(
            onClick = {
                val intent = Intent(activity, LoginForm::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Login")
        }
    }
}