package org.myq

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.protocol.client.error.RemoteClientException
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.Track
import java.sql.Connection

object SpotifyManager {
    private lateinit var connectionParams: ConnectionParams
    private var remote: SpotifyAppRemote? = null
    private var api: SpotifyService? = null

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
                makeToast("Connected to Spotify", context)
                isConnected = true
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyManager", throwable.message, throwable)
                if (throwable is CouldNotFindSpotifyApp) {
                    makeToast("Spotify App not installed!  Please install the App to play music!", context)
                } else if (throwable is NotLoggedInException) {
                    makeToast("Not signed in with Spotify!  Please sign in to the Spotify app to continue!", context)
                } else {
                    makeToast("Failed to connect to Spotify!", context)
                }
            }
        }

        /* connect to Spotify using Android SDK */
        if (remote == null) SpotifyAppRemote.disconnect(remote)
        SpotifyAppRemote.setDebugMode(true)
        SpotifyAppRemote.connect(context, connectionParams, connectionListener)
    }

    fun initSpotifyWeb(accessToken: String) {
        api = SpotifyApi().setAccessToken(accessToken).service
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
            remote!!.playerApi.play(uri)
        }
    }

    fun resume() {
        if(remote == null) return
        remote!!.playerApi.resume()
    }

    fun pause() {
        if(remote == null) return
        remote!!.playerApi.pause()
    }

    fun getPlayerState(): com.spotify.protocol.client.Result<PlayerState>? {
        if(remote == null) return null
        return remote!!.playerApi.playerState.await()
    }

    fun searchTrack(query: String): List<Track> {
        val searchResults = api!!.searchTracks(query).tracks.items

        if (searchResults != null) {
            return searchResults
        } else {
            Log.e("SpotifyManager", "No songs found!")
            return emptyList()
        }
    }
}