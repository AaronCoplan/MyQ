package org.myq

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
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
            QueueManager.hasOwnQueue(user = currentUser, ifQueueExists = {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.queue_exists))
                    .setMessage(R.string.rejoin_or_replace)
                    .setNeutralButton(getString(R.string.rejoin)) { _, _ ->
                        QueueManager.joinQueue(currentUser.uid, onSuccess = {
                            advanceToNextActivity()
                        }, onFailure = {
                            makeToast(getString(R.string.rejoin_error), this)
                        })
                    }
                    .setPositiveButton(getString(R.string.replace)) { _, _ ->
                        QueueManager.createQueue(user = currentUser)
                        advanceToNextActivity()
                    }
                    .show()
            }) {
                QueueManager.createQueue(user = currentUser)
            }
        }

        joinQueueButton.setOnClickListener {
            val joinCodeEditText = EditText(this)
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.join_queue))
                .setMessage(getString(R.string.enter_code))
                .setView(joinCodeEditText)
                .setPositiveButton("Join") { dialog, which ->
                    val joinCode = joinCodeEditText.text.toString()
                    QueueManager.joinQueue(joinCode, onSuccess = {
                        advanceToNextActivity()
                    }, onFailure = {
                        makeToast(getString(R.string.join_error), this)
                    })
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                .show()
        }
    }

    // go to QueueViewActivity
    private fun advanceToNextActivity() {
        val queueViewActivity = Intent(this, QueueViewActivity::class.java)
        startActivity(queueViewActivity)
    }
}
