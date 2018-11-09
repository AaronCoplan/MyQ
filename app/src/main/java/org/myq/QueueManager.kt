package org.myq

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

object QueueManager {

    private val db = FirebaseDatabase.getInstance()

    private var counter = 0

    // return true if user already has a queue, false otherwise
    fun hasOwnQueue(user: FirebaseUser): Boolean {
        val reference = db.reference.child(user.uid)
        println(reference)
        synchronized(counter) {
            reference.setValue("SUP THERE $counter")
            ++counter
        }
        return false
    }
}