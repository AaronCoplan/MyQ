package org.myq

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.PasswordTransformationMethod
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
        // Fabric.with(this, Crashlytics())
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

                        makeToast(getString(R.string.login_success), this)
                        advanceToNextActivity()
                    }
                    .addOnFailureListener { exception ->
                        val bundle = Bundle()
                        bundle.putBoolean("login_success", false)
                        firebaseAnalytics.logEvent("login", bundle)

                        exception.printStackTrace()
                        when(exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                makeToast(getString(R.string.login_error, exception.message), this)
                            }
                            is FirebaseAuthInvalidUserException -> {
                                makeToast(getString(R.string.login_bad_credentials), this)
                            }
                            else -> {
                                makeToast(getString(R.string.login_failure), this)
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
                confirmPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()

                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.confirm_password))
                    .setMessage(getString(R.string.reenter_password))
                    .setView(confirmPasswordEditText)
                    .setPositiveButton(getString(getString(R.string.signup))) { _, which ->
                        val confirmPassword = confirmPasswordEditText.text.toString()
                        if(!confirmPassword.isNullOrEmpty() && !password.isNullOrEmpty() && password.equals(confirmPassword)) {
                            firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    val bundle = Bundle()
                                    bundle.putBoolean("signup_success", true)
                                    firebaseAnalytics.logEvent("signup", bundle)

                                    makeToast(getString(R.string.signup_success), this)
                                    advanceToNextActivity()
                                } else {
                                    val bundle = Bundle()
                                    bundle.putBoolean("signup_success", false)
                                    firebaseAnalytics.logEvent("signup", bundle)

                                    val exception = task.exception
                                    if(exception != null) {
                                        exception.printStackTrace()
                                        if(exception is FirebaseAuthUserCollisionException) {
                                            makeToast(getString(R.string.signup_dup), this)
                                        } else {
                                            makeToast(getString(R.string.signup_fail), this)
                                        }
                                    } else {
                                        makeToast(getString(R.string.signup_fail), this)
                                    }
                                }
                            }
                        } else {
                            makeToast(getString(R.string.mismatch_password), this)
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                    .show()
            }
        }
    }

    private fun isUsernameOrPasswordEmpty(username: String, password: String): Boolean {
        if(username.isNullOrEmpty()) {
            makeToast(getString(R.string.user_empty), this)
            return true
        } else if(password.isNullOrEmpty()) {
            makeToast(getString(R.string.password_empty), this)
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
