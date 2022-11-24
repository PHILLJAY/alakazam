package com.example.alakazam

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

    private var registerStatus = false

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.login_register_activity)

        auth = Firebase.auth

        auth.signOut()

        db = Firebase.firestore

        emailField = findViewById(R.id.editTextTextEmailAddress)
        passwordField = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.loginButton)

        updateForm()

        loginButton.setOnClickListener{
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if(registerStatus){
                // Create new user
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Log.d("Auth", "CreateUserWithEmail:sucess")

                            val userData = hashMapOf(
                                "following" to listOf<String>()
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

                        }

                    }

            }
        }

    }

    private fun updateForm() {
        TODO("Not yet implemented")
    }
}



