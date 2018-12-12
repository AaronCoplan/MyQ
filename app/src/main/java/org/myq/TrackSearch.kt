package org.myq

import android.content.Context
import android.os.AsyncTask

class TrackSearch(
    context: Context,
    private val onSuccessListener: (List<Song>) -> Unit,
    private val onErrorListener: (Exception) -> Unit
): AsyncTask<String, Void, List<Song>?>() {

    override fun doInBackground(vararg params: String?): List<Song>? {
        val param = params[0]
        if(param == null) return null

        return try {
            SpotifyManager.searchTrack(param).map { track ->
                trackToSong(track)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: List<Song>?) {
        if(result == null) {
            onErrorListener.invoke(Exception("Failed to list songs!"))
        } else {
            onSuccessListener.invoke(result)
        }
    }
}