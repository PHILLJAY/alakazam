package com.example.alakazam

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


class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        Log.d("Checkpoint", "lmao oncreate")

        auth = Firebase.auth

        auth.signOut()

        db = Firebase.firestore

        emailField = findViewById(R.id.editTextTextEmailAddress)
        passwordField = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.loginButton)



        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            // Create new user , if it fails log them in instead lol
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
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
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
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



