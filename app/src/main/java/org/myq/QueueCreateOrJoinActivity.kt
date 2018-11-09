package org.myq

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
                makeToast("QUEUE ALREADY EXISTS", this)
            }, ifNoQueueExists = {
                QueueManager.createQueue(currentUser)
                makeToast("NO QUEUE EXISTS, CREATING ONE", this)
            })
        }
    }
}
