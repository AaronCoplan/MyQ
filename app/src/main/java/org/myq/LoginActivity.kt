package org.myq

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var preferences: SharedPreferences

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var rememberUsernameSwitch: Switch
    private lateinit var rememberPasswordSwitch: Switch
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_login)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()

        // FIXME: REMOVE DEBUG HACK
        if(firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }

        if(firebaseAuth.currentUser != null) {
            // already authenticated
            advanceToNextActivity()
        }

        preferences = getSharedPreferences("MyQ", Context.MODE_PRIVATE)

        rememberUsernameSwitch = findViewById(R.id.rememberUsernameSwitch)
        rememberPasswordSwitch = findViewById(R.id.rememberPasswordSwitch)

        rememberUsernameSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            val username = usernameEditText.text.toString()

            if(isChecked) {
                if(username.isNullOrEmpty()) {
                   buttonView.isChecked = false
                } else {
                    preferences.edit().putString("USERNAME", username).apply()
                }
            } else {
                preferences.edit().remove("USERNAME").apply()
            }
        }

        rememberPasswordSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            val password = passwordEditText.text.toString()

            if(isChecked) {
                if(password.isNullOrEmpty()) {
                    buttonView.isChecked = false
                } else {
                    preferences.edit().putString("PASSWORD", password).apply()
                }
            }else {
                preferences.edit().remove("PASSWORD").apply()
            }
        }

        usernameEditText = findViewById(R.id.usernameEditText)
        if(preferences.contains("USERNAME")) {
            usernameEditText.setText(preferences.getString("USERNAME", ""))
            rememberUsernameSwitch.isChecked = true
        }

        passwordEditText = findViewById(R.id.passwordEditText)
        if(preferences.contains("PASSWORD")) {
            passwordEditText.setText(preferences.getString("PASSWORD", ""))
            rememberPasswordSwitch.isChecked = true
        }


        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(!isUsernameOrPasswordEmpty(username, password)) {
                firebaseAuth.signInWithEmailAndPassword(username, password)
                    .addOnSuccessListener { result ->
                        val bundle = Bundle()
                        bundle.putBoolean("login_success", true)
                        firebaseAnalytics.logEvent("login", bundle)

                        makeToast("Logged in successfully!", this)
                        advanceToNextActivity()
                    }
                    .addOnFailureListener { exception ->
                        val bundle = Bundle()
                        bundle.putBoolean("login_success", false)
                        firebaseAnalytics.logEvent("login", bundle)

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
                val confirmPasswordEditText = EditText(this)
                AlertDialog.Builder(this)
                    .setTitle("Confirm Password")
                    .setMessage("Please re-enter your password to finish signing up!")
                    .setView(confirmPasswordEditText)
                    .setPositiveButton("Sign Up") { _, which ->
                        val confirmPassword = confirmPasswordEditText.text.toString()
                        if(!confirmPassword.isNullOrEmpty() && !password.isNullOrEmpty() && password.equals(confirmPassword)) {
                            firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    val bundle = Bundle()
                                    bundle.putBoolean("signup_success", true)
                                    firebaseAnalytics.logEvent("signup", bundle)

                                    makeToast("Signed up successfully!", this)
                                    advanceToNextActivity()
                                } else {
                                    val bundle = Bundle()
                                    bundle.putBoolean("signup_success", false)
                                    firebaseAnalytics.logEvent("signup", bundle)

                                    val exception = task.exception
                                    if(exception != null) {
                                        exception.printStackTrace()
                                        if(exception is FirebaseAuthUserCollisionException) {
                                            makeToast("User already signed up!  Please login.", this)
                                        } else {
                                            makeToast("Signup failed!  Please try again.", this)
                                        }
                                    } else {
                                        makeToast("Signup failed!  Please try again.", this)
                                    }
                                }
                            }
                        } else {
                            makeToast("Error: Passwords do not match!", this)
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ -> }
                    .show()
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

    // go to QueueCreateOrJoinActivity
    private fun advanceToNextActivity() {
        val queueSetupActivityIntent = Intent(this, QueueCreateOrJoinActivity::class.java)
        startActivity(queueSetupActivityIntent)
        finish() // we don't want logged in users to be able to use the back button to get back to the login / signup screen
    }
}
