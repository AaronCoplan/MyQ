package org.myq

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.lang.ref.Reference

data class Song (
    val name: String
)

data class Queue (
    val joinCode: String,
    val songs: List<Song>
)

object QueueManager {

    private val db = FirebaseDatabase.getInstance()

    private fun getQueueReference(user: FirebaseUser): DatabaseReference {
        return db.reference.child(user.uid)
    }

    // execute ifQueueExists() if user already has a queue, ifNoQueueExists() otherwise
    fun hasOwnQueue(user: FirebaseUser, ifQueueExists: () -> Unit, ifNoQueueExists: () -> Unit) {
        val reference = getQueueReference(user)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(queueSnapshot: DataSnapshot) {
                if(queueSnapshot.exists()) {
                   ifQueueExists()
                } else {
                    ifNoQueueExists()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // ignore
            }
        })
    }

    // creates a queue for a user
    fun createQueue(user: FirebaseUser) {
        val reference = getQueueReference(user)
        reference.setValue(Queue(joinCode = "SUP", songs = listOf()))
    }
}