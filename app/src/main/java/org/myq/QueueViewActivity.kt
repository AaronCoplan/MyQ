package org.myq

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class QueueViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyQueueTextView: TextView
    private lateinit var addButton: FloatingActionButton
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_view)

        recyclerView = findViewById(R.id.queueViewRecyclerView)
        emptyQueueTextView = findViewById(R.id.emptyQueueTextView)
        addButton = findViewById(R.id.plusButton)

        addButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        val activeQueueID = QueueManager.getActiveQueueID()
        if(activeQueueID != null && activeQueueID.equals(currentUser!!.uid)) {
            // you created the queue, initialize playback
            SpotifyManager.connect(this, getString(R.string.spotify_client_id), getString(R.string.spotify_redirect_uri))
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        QueueManager.subscribeToSongList(callback = { songList ->
            if (songList.isEmpty()) {
                emptyQueueTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.adapter = SongListAdapter(songList, null)
                emptyQueueTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }, failureCallback = {
            makeToast("Error: Failed to update song list!", this)
        })
    }
}
