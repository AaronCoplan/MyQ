package org.myq

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

private lateinit var firebaseAuth: FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser != null) {
            // already authenticated, go to QueueSetupActivity
            val queueSetupActivityIntent = Intent(this, QueueSetupActivity::class.java)
            startActivity(queueSetupActivityIntent)
            finish(); // we don't want logged in users to be able to use the back button to get back to the login / signup screen
        }
    }
}
