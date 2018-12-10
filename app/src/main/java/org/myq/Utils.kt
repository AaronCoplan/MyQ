package org.myq

import android.content.Context
import android.widget.Toast
import com.spotify.protocol.types.Track
import kaaes.spotify.webapi.android.models.Track

fun makeToast(msg: String, context: Context) {
    val toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
    toast.show()
}

fun trackToSong(track: com.spotify.protocol.types.Track) {
    if (track != null) {
        Song(track.name, track.artist.name, track.uri)
    } else {
        null
    }
}

fun trackToSong(track: kaaes.spotify.webapi.android.models.Track) {
    if (track != null) {
        Song(track.name, track.artists[0].name, track.uri)
    } else {
        null
    }
}