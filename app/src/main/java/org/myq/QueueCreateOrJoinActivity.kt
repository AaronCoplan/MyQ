package org.myq

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class QueueCreateOrJoinActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var createQueueButton: Button
    private lateinit var joinQueueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_create_or_join)

        firebaseAuth = FirebaseAuth.getInstance()

        createQueueButton = findViewById(R.id.createQueueButton)
        joinQueueButton = findViewById(R.id.joinQueueButton)

        val currentUser = firebaseAuth.currentUser!!
        createQueueButton.setOnClickListener {
            QueueManager.hasOwnQueue(currentUser, ifQueueExists = {
                AlertDialog.Builder(this)
                    .setTitle("Queue Exists")
                    .setMessage("Would you like to REJOIN the existing queue or REPLACE with a new queue?")
                    .setNeutralButton("Rejoin", { dialog, which ->
                        QueueManager.joinQueue(currentUser.uid, onSuccess = {
                            advanceToNextActivity()
                        }, onFailure = {
                            makeToast("Error: Failed to rejoin queue!", this)
                        })
                    })
                    .setPositiveButton("Replace", { dialog, which ->
                        QueueManager.createQueue(currentUser)
                        advanceToNextActivity()
                    })
                    .show()
            }, ifNoQueueExists = {
                QueueManager.createQueue(currentUser)
            })
        }
    }

    // go to QueueViewActivity
    private fun advanceToNextActivity() {
        val queueViewActivity = Intent(this, QueueViewActivity::class.java)
        startActivity(queueViewActivity)
    }
}
