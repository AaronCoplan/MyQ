package org.myq

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
// import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class QueueViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyQueueTextView: TextView
    private lateinit var addButton: FloatingActionButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var pauseButton: FloatingActionButton
    private lateinit var nextButton: FloatingActionButton
    private lateinit var currentUser: FirebaseUser
    private var currentSong: Song? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_view)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        recyclerView = findViewById(R.id.queueViewRecyclerView)
        emptyQueueTextView = findViewById(R.id.emptyQueueTextView)

        addButton = findViewById(R.id.plusButton)
        addButton.setOnClickListener {
            firebaseAnalytics.logEvent("add_button_click", null)
            startActivity(Intent(this, SearchActivity::class.java))
        }

        playButton = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            firebaseAnalytics.logEvent("play_button_click", null)
            SpotifyManager.resume()
        }

        pauseButton = findViewById(R.id.pauseButton)
        pauseButton.setOnClickListener {
            firebaseAnalytics.logEvent("pause_ button_click", null)
            SpotifyManager.pause()
        }

        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            QueueManager.popQueue { song ->
                if(song == null) {
                    val bundle = Bundle()
                    bundle.putBoolean("queue_is_empty", true)
                    firebaseAnalytics.logEvent("next_button_click", bundle)
                    makeToast(getString(R.string.no_song_error), this)
                } else {
                    val bundle = Bundle()
                    bundle.putBoolean("queue_is_empty", false)
                    firebaseAnalytics.logEvent("next_button_click", bundle)
                    currentSong = song
                    SpotifyManager.play(song.uri)
                    println("[PLAYING SONG] ${song.title}")
                }
            }
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
            makeToast(getString(R.string.update_fail), this)
        })

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val activeQueueID = QueueManager.getActiveQueueID()

        if(activeQueueID != null && activeQueueID.equals(currentUser!!.uid)) {
            // you created the queue, initialize playback
            SpotifyManager.connect(
                    this,
                    getString(R.string.spotify_client_id),
                    getString(R.string.spotify_redirect_uri)
            )

            var skipNextIteration = false
            Thread({
                while(true) {
                    Thread.sleep(5000)
                    if(skipNextIteration) {
                        skipNextIteration = false
                        continue
                    }
                    val playerStateResult = SpotifyManager.getPlayerState()
                    if(playerStateResult == null) {
                        // pretty sure at this point you will be disconnected and need to reconnect
                        Log.e("PlayerThread", "Player result is null, pretty sure you are disconnected")
                    } else if(playerStateResult != null && playerStateResult.isSuccessful) {
                        val playerState = playerStateResult.data
                        if(playerState.track == null) {
                            println("Track is null, play next song")
                            QueueManager.popQueue { song ->
                                if(song == null) {
                                    println("[NOTHING PLAYING] Song is null")
                                } else {
                                    skipNextIteration = true
                                    currentSong = song
                                    SpotifyManager.play(song.uri)
                                    println("[PLAYING SONG] ${song.title}")
                                }
                            }
                        } else {
                            val timeDiff = playerState.track.duration - playerState.playbackPosition
                            println("Time Diff: $timeDiff")
                            if(playerState.track.name != null && timeDiff != 0L && timeDiff < (10 * 1000)) {
                                println("Skip to next song")
                                QueueManager.popQueue { song ->
                                    if(song == null) {
                                        println("[NOTHING PLAYING] Song is null")
                                    } else {
                                        skipNextIteration = true
                                        currentSong = song
                                        SpotifyManager.play(song.uri)
                                        println("[PLAYING SONG] ${song.title}")
                                    }
                                }
                            }
                        }
                    }
                }
            }).start()
        } else {
            println("YOU DONT OWN THE QUEUE")
        }
    }
}
