package org.myq

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService
import java.sql.Connection

class SpotifyManager {
    private lateinit var connectionParams: ConnectionParams
    private lateinit var remote: SpotifyAppRemote
    private lateinit var api: SpotifyService

    private var isConnected = false

    /* call in onStart() */
    /* connect to spotify */
    fun connect(context: Context, clientID: String, callbackURI: String) {

        /* build connection parameters for Android SDK */
        connectionParams = ConnectionParams.Builder(clientID).setRedirectUri(callbackURI).showAuthView(true)
            .build()

        val connectionListener = object : Connector.ConnectionListener {
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                remote = spotifyAppRemote
                Log.e("SpotifyManager", "Connected to Spotify!")

                isConnected = true
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyManager", throwable.message, throwable)
            }
        }

        /* connect to Spotify using Android SDK */
        SpotifyAppRemote.connect(context, connectionParams, connectionListener)

        /* connect to Spotify Web API */
        api = SpotifyApi().service
    }

    /* call from onStop() */
    /* disconnect from spotify */
    fun disconnect() {
        if (isConnected) {
            SpotifyAppRemote.disconnect(remote)
        }
    }

    /* play song, playlist, etc. with given uri code */
    fun play(uri: String) {
        if (isConnected) {
            remote.playerApi.play(uri)
        }
    }

    /* fetch current track being played */
    fun getCurrentTrack() {
        if (isConnected) {
            remote.playerApi.subscribeToPlayerState().setEventCallback { playerState ->
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

    fun searchTrack(query: String) {
        val searchResults = api.searchTracks(query).tracks.items

        if (searchResults != null) {
            searchResults
        } else {
            Log.e("SpotifyManager", "No songs found!")
            null
        }
    }
}