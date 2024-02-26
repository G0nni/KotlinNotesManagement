package com.example.kotlinnotesmanagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
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
import androidx.core.content.ContextCompat
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper
import com.example.kotlinnotesmanagement.ui.theme.KotlinNotesManagementTheme
import java.util.concurrent.Executor

class LoginForm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinNotesManagementTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val executor: Executor = ContextCompat.getMainExecutor(this@LoginForm)
                    val biometricPrompt = BiometricPrompt(this@LoginForm, executor, authenticationCallback)
                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Connexion par empreinte digitale")
                        .setSubtitle("Connectez-vous avec votre empreinte digitale")
                        .setNegativeButtonText("Annuler")
                        .build()
                    biometricPrompt.authenticate(promptInfo)
                    LoginScreen(this@LoginForm, biometricPrompt, promptInfo)
                }
            }
        }
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            showToast("Erreur d'authentification : $errString")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            // Connexion réussie, rediriger vers l'écran principal
            val intent = Intent(this@LoginForm, MainActivity::class.java)
            startActivity(intent)
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            showToast("Échec de l'authentification.")
        }
    }

    // Fonction pour afficher un toast
    fun showToast(message: String) {
        Toast.makeText(this@LoginForm, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun LoginScreen(
    activity: ComponentActivity,
    biometricPrompt: BiometricPrompt,
    promptInfo: BiometricPrompt.PromptInfo
) {
    val focusManager = LocalFocusManager.current
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = setEmail,
            label = { Text("Email") },
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
                biometricPrompt.authenticate(promptInfo)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Login with Biometrics")
        }

        Button(
            onClick = {
                val userHelper = DatabaseHelper(activity)
                val user = userHelper.getUser(email)
                if (user != null && user.password == password) {
                    // Connexion réussie, stocker l'ID de l'utilisateur dans les SharedPreferences
                    val sharedPreferences = activity.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("loggedInUserId", user.id)
                    editor.apply()

                    // Connexion réussie, rediriger vers l'écran principal
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                } else {
                    // Si l'utilisateur est null ou que le mot de passe est incorrect, afficher un message d'erreur
                    showToast(activity, "Identifiants incorrects")
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Login")
        }

        Button(
            onClick = {
                val intent = Intent(activity, RegisterForm::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Register")
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

