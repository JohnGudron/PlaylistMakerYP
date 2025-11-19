package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.media.MediaActivity
import com.google.gson.Gson

const val TRACK = "track"

class SearchActivity : AppCompatActivity() {

    private val searchInteractor = Creator.provideTracksInteractor()
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private var searchText = ""
    private var lastResponse = ""
    private var history = mutableListOf<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private var itemClickAllowed = true

    private lateinit var input: EditText
    private lateinit var clearButton: ImageView
    private lateinit var clearHistoryButton: Button
    private lateinit var backBtn: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var errorScreen: View
    private lateinit var nothingFindScreen: View
    private lateinit var updateBtn: Button
    private lateinit var searchHistory: View
    private lateinit var historyRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var historyAdapter: TrackAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private var searchRunnable: Runnable = Runnable { makeSearch(searchText) }


    private val tracks = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        progressBar = findViewById(R.id.progress_bar)
        input = findViewById(R.id.input)
        clearButton = findViewById(R.id.clear_btn)
        clearHistoryButton = findViewById(R.id.clear_history_btn)
        backBtn = findViewById(R.id.backBtn)
        recycler = findViewById(R.id.track_recycler)
        errorScreen = findViewById(R.id.error_placeholder)
        nothingFindScreen = findViewById(R.id.nothing_find_screen)
        updateBtn = findViewById(R.id.update_btn)
        searchHistory = findViewById(R.id.search_history)
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(getSharedPreferences(PREFERENCES, MODE_PRIVATE))
        historyRecycler = findViewById(R.id.history_recycler)

        searchHistoryInteractor.getSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(searchHistory: List<Track>) {
                history.addAll(searchHistory)
            }
        })

        adapter = TrackAdapter { track ->
            if (itemClickDebounce()) {
                val intent = Intent(this, MediaActivity::class.java)
                intent.putExtra(TRACK, Gson().toJson(track))
                startActivity(intent)
                searchHistoryInteractor.addTrackToHistory(track, object : SearchHistoryInteractor.SearchHistoryConsumer {
                    override fun consume(searchHistory: List<Track>) {
                        history.clear()
                        history.addAll(searchHistory)
                        historyAdapter.notifyDataSetChanged()
                    }
                })
            }

        }
        historyAdapter = TrackAdapter { track ->
            if (itemClickDebounce()) {
                val intent = Intent(this, MediaActivity::class.java)
                intent.putExtra(TRACK, Gson().toJson(track))
                startActivity(intent)
                searchHistoryInteractor.addTrackToHistory(track, object : SearchHistoryInteractor.SearchHistoryConsumer {
                    override fun consume(searchHistory: List<Track>) {
                        history.clear()
                        history.addAll(searchHistory)
                        historyAdapter.notifyDataSetChanged()
                    }
                })
            }
        }

        input.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryInteractor.getSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(searchHistory: List<Track>) {
                    history.clear()
                    history.addAll(searchHistory)
                    historyAdapter.notifyDataSetChanged()
                }
            })
            if (hasFocus && input.text.isEmpty() && history.isNotEmpty()) {
                showHistoryView()
            } else {
                recycler.visibility = View.VISIBLE
                searchHistory.visibility = View.GONE
            }
        }

        input.setText(searchText)
        input.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
            clearButton.visibility=clearButtonVisibility(text)
            searchDebounce()
            showProgress()
        }


        input.doAfterTextChanged { text ->
            searchText = text.toString()
            if (text.isNullOrEmpty()) {
                showHistoryView()
            }
        }

        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        adapter.tracks = tracks

        historyRecycler.adapter = historyAdapter
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyAdapter.tracks = history

        clearButton.setOnClickListener {
            searchText = ""
            lastResponse = ""
            input.setText(searchText)
            tracks.clear()
            adapter.notifyDataSetChanged()
            input.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(input.windowToken, 0)
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(searchHistory: List<Track>) {
                    history.clear()
                    history.addAll(searchHistory)
                    historyAdapter.notifyDataSetChanged()
                }
            })
            hideHistoryView()
        }

        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                makeSearch(input.text.toString())
                hideHistoryView()
                true
            }
            false
        }

        updateBtn.setOnClickListener {
            makeSearch(lastResponse)
            recycler.visibility = View.VISIBLE
            errorScreen.visibility = View.GONE
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        input.setText(savedInstanceState.getString(SEARCH_TEXT))
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showErrorSearch() {
        recycler.visibility = View.GONE
        errorScreen.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showNothingFound() {
        recycler.visibility = View.GONE
        nothingFindScreen.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showHistoryView() {
        recycler.visibility = View.GONE
        searchHistory.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun hideHistoryView() {
        recycler.visibility = View.VISIBLE
        searchHistory.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showProgress() {
        recycler.visibility = View.GONE
        searchHistory.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        nothingFindScreen.visibility = View.GONE
    }

    private fun showRecycler() {
        recycler.visibility = View.VISIBLE
        searchHistory.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun makeSearch(str: String) {
        lastResponse = str
        if (str.isNotEmpty()) {

            searchInteractor.searchTracks(str, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    handler.post {
                        tracks.clear()
                        if (foundTracks.isNotEmpty()) {
                            tracks.addAll(foundTracks)
                            adapter.notifyDataSetChanged()
                            showRecycler()
                        } else showNothingFound()
                    }
                }
            })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY )
    }

    private fun itemClickDebounce(): Boolean {
        val cur = itemClickAllowed
        if (itemClickAllowed) {
            itemClickAllowed = false
            handler.postDelayed({itemClickAllowed = true}, ITEM_CLICK_DEBOUNCE)
        }
        return cur
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEBOUNCE_DELAY = 1500L
        const val ITEM_CLICK_DEBOUNCE = 1000L
    }
}