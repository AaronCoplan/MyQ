package org.myq

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import java.sql.Connection

class SpotifyManager {
    private lateinit var connectionParams: ConnectionParams
    private lateinit var spotifyRemote: SpotifyAppRemote

    private var isConnected = false

    /* connect to spotify */
    /* call in onStart() */
    fun connect(context: Context, clientID: String, callbackURI: String) {
        connectionParams = ConnectionParams.Builder(clientID).setRedirectUri(callbackURI).showAuthView(true)
            .build()

        val connectionListener = object : Connector.ConnectionListener {
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                spotifyRemote = spotifyAppRemote
                Log.e("SpotifyManager", "Connected to Spotify!")

                isConnected = true
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyManager", throwable.message, throwable)
            }
        }

        SpotifyAppRemote.connect(context, connectionParams, connectionListener)
    }

    /* disconnect from spotify */
    /* call in onStop() */
    fun disconnect() {
        if (isConnected) {
            SpotifyAppRemote.disconnect(spotifyRemote)
        }
    }

    /* play song, playlist, etc. with given uri code */
    fun play(uri: String) {
        if (isConnected) {
            spotifyRemote.playerApi.play(uri)
        }
    }

    /* fetch current track being played */
    fun getCurrentTrack() {
        if (isConnected) {
            spotifyRemote.playerApi.subscribeToPlayerState().setEventCallback { playerState ->
                val track = playerState.track
                if (track != null) {
                    Log.e("SpotifyManager", track.name + " by " + track.artist.name)
                    track
                } else {
                    Log.e("SpotifyManager", "No song playing!")
                    null
                }
            }
        }
    }
}