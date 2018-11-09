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

        createQueueButton.setOnClickListener {
            if(QueueManager.hasOwnQueue(firebaseAuth.currentUser!!)) {
                // users may only possess one queue at a time
                // user already has a queue, prompt to delete and start new queue or rejoin old queue
            }
        }
    }
}
