package com.example.alakazam

// Import necessary libraries and packages
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Define the LoginRegisterActivity class that inherits from AppCompatActivity
class LoginRegisterActivity : AppCompatActivity() {
    // Declare variables for the email field, password field, and login button
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button

    // Declare variables for Firebase authentication and Firestore database
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Override the onCreate function which is called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        // Log a debug message
        Log.d("Checkpoint", "lmao oncreate")

        // Initialize Firebase authentication
        auth = Firebase.auth

        // Sign out the current user
        auth.signOut()

        // Initialize Firestore database
        db = Firebase.firestore

        // Find the email field, password field, and login button in the layout
        emailField = findViewById(R.id.editTextTextEmailAddress)
        passwordField = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.loginButton)

        // Set an onClick listener for the login button
        loginButton.setOnClickListener {
            // Get the email and password entered by the user
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            // Try to create a new user with the entered email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If the user is created successfully, log a message and add the user to the database
                        Log.d("Auth", "CreateUserWithEmail:sucess")

                        val userData = hashMapOf(
                            "favorites" to listOf<String>()
                        )
                        db.collection("users").document(auth.currentUser!!.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d(
                                    "New User",
                                    "New user created in db"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w("New User", "Error adding document", e)
                            }
                        // Start the MainActivity
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // If the user creation fails, try to sign in the user with the entered email and password
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // If the sign in is successful, log a message and start the MainActivity
                                    Log.d("Auth", "signInWithEmail:success")
                                    //
                                    startActivity(Intent(this, MainActivity::class.java))
                                } else {
                                    Log.w("Auth", "signInWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }

                }
        }
    }
}
