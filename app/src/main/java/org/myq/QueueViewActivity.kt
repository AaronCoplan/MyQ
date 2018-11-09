package org.myq

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class QueueViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyQueueTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_view)

        recyclerView = findViewById(R.id.queueViewRecyclerView)
        emptyQueueTextView = findViewById(R.id.emptyQueueTextView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        QueueManager.subscribeToSongList { songList ->
            if (songList.isEmpty()) {
                emptyQueueTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.adapter = QueueAdapter(songList)
                emptyQueueTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}
