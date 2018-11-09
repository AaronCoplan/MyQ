package org.myq

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        // FIXME: REMOVE DEBUG HACK
        if(firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }

        if(firebaseAuth.currentUser != null) {
            // already authenticated
            advanceToNextActivity()
        }

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(!isUsernameOrPasswordEmpty(username, password)) {
                firebaseAuth.signInWithEmailAndPassword(username, password)
                    .addOnSuccessListener { result ->
                        makeToast("Logged in successfully!", this)
                        advanceToNextActivity()
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                        when(exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                makeToast("Login Failed: ${exception.message}", this)
                            }
                            is FirebaseAuthInvalidUserException -> {
                                makeToast("Login Failed: Email or password did not match!", this)
                            }
                            else -> {
                                makeToast("Login Failed!", this)
                            }
                        }

                    }
            }
        }

        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(!isUsernameOrPasswordEmpty(username, password)) {
                firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        makeToast("Signed up successfully!", this)
                        advanceToNextActivity()
                    } else {
                        val exception = task.exception
                        if(exception != null) {
                            exception.printStackTrace()
                            if(exception is FirebaseAuthUserCollisionException) {
                                makeToast("User already signed up!  Please login.", this)
                            } else {
                                makeToast("Signup failed!  Please try again.", this)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isUsernameOrPasswordEmpty(username: String, password: String): Boolean {
        if(username.isNullOrEmpty()) {
            makeToast("Username cannot be empty!", this)
            return true
        } else if(password.isNullOrEmpty()) {
            makeToast("Password cannot be empty!", this)
            return true
        } else {
            return false
        }
    }

    // go to QueueSetupActivity
    private fun advanceToNextActivity() {
        val queueSetupActivityIntent = Intent(this, QueueSetupActivity::class.java)
        startActivity(queueSetupActivityIntent)
        finish() // we don't want logged in users to be able to use the back button to get back to the login / signup screen
    }
}
