package org.myq

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var songList: List<Song>
    private lateinit var noSearchResultsTextView: TextView
    private lateinit var searchButton: Button
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        noSearchResultsTextView = findViewById(R.id.emptySearchResultsTextView)
        searchButton = findViewById(R.id.searchButton)
        searchEditText = findViewById(R.id.searchEditText)

        searchButton.setOnClickListener{
            val searchText: String = searchEditText.text.toString()
            searchButton.isEnabled = false
            searchEditText.isEnabled = false

            // do search

            searchButton.isEnabled = true
            searchEditText.isEnabled = true
            renderSongList()
        }

        songList = ArrayList()

        recyclerView = findViewById(R.id.searchResultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        renderSongList()
    }

    private fun renderSongList() {
        if(songList.isEmpty()) {
            noSearchResultsTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.adapter = SongListAdapter(songList)
            noSearchResultsTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
