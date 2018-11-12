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
    private var activeQueueID: String? = null

    private fun getQueueReference(user: FirebaseUser): DatabaseReference {
        return db.reference.child(user.uid)
    }

    private fun updateActiveQueueID(newActiveQueueID: String?) {
        println("\nUPDATED ACTIVE QUEUE ID: $newActiveQueueID\n")
        activeQueueID = newActiveQueueID
    }

    // execute ifQueueExists() if user already has a queue, ifNoQueueExists() otherwise
    fun hasOwnQueue(user: FirebaseUser, ifQueueExists: () -> Unit, ifNoQueueExists: () -> Unit) {
        val reference = getQueueReference(user)
        doesQueueExist(reference, ifQueueExists, ifNoQueueExists)
    }

    fun doesQueueExist(reference: DatabaseReference, ifQueueExists: () -> Unit, ifNoQueueExists: () -> Unit) {
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
        reference.setValue(Queue(joinCode = user.uid, songs = listOf()))
        updateActiveQueueID(user.uid)
    }

    // joins a queue with the specified code, calling onSuccess and onFailure based on result
    fun joinQueue(joinCode: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val reference = db.reference.child(joinCode)
        doesQueueExist(reference, ifQueueExists = {
            updateActiveQueueID(joinCode)
            onSuccess()
        }, ifNoQueueExists = {
            onFailure()
        })
    }
}