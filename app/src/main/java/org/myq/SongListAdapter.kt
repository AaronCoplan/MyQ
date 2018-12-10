package org.myq

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SongListAdapter(private val songs: List<Song>, private val rowClickListener: ((Song) -> Unit)?): RecyclerView.Adapter<SongListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View = LayoutInflater.from(p0.context)
            .inflate(R.layout.layout_single_song, p0, false)
        return ViewHolder(view)
    }

    interface OnRowClickListener {
        fun onRowItemClicked(song: Song)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val currentSong = songs[p1]
        p0.titleTextView.text = currentSong.title
        p0.artistTextView.text = currentSong.artist
        p0.itemView.setOnClickListener {
            if(rowClickListener != null) {
                rowClickListener.invoke(currentSong)
            }
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.title)
        val artistTextView: TextView = view.findViewById(R.id.artist)
    }
}