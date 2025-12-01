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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Creator
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.media.MediaActivity
import com.google.gson.Gson

const val TRACK = "track"

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchInteractor = Creator.provideTracksInteractor()
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private var searchText = ""
    private var lastResponse = ""
    private var history = mutableListOf<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private var itemClickAllowed = true

    private lateinit var historyAdapter: TrackAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private var searchRunnable: Runnable = Runnable { makeSearch(searchText) }


    private val tracks = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(getSharedPreferences(PREFERENCES, MODE_PRIVATE))

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

        binding.input.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryInteractor.getSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(searchHistory: List<Track>) {
                    history.clear()
                    history.addAll(searchHistory)
                    historyAdapter.notifyDataSetChanged()
                }
            })
            if (hasFocus && binding.input.text.isEmpty() && history.isNotEmpty()) {
                showHistoryView()
            } else {
                binding.trackRecycler.visibility = View.VISIBLE
                binding.searchHistory.visibility = View.GONE
            }
        }

        binding.input.setText(searchText)
        binding.input.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
            binding.clearBtn.visibility=clearButtonVisibility(text)
            searchDebounce()
            showProgress()
        }


        binding.input.doAfterTextChanged { text ->
            searchText = text.toString()
            if (text.isNullOrEmpty()) {
                showHistoryView()
            }
        }

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.trackRecycler.adapter = adapter
        binding.trackRecycler.layoutManager = LinearLayoutManager(this)
        adapter.tracks = tracks

        binding.historyRecycler.adapter = historyAdapter
        binding.historyRecycler.layoutManager = LinearLayoutManager(this)
        historyAdapter.tracks = history

        binding.clearBtn.setOnClickListener {
            searchText = ""
            lastResponse = ""
            binding.input.setText(searchText)
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.input.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.input.windowToken, 0)
        }

        binding.clearHistoryBtn.setOnClickListener {
            searchHistoryInteractor.clearSearchHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(searchHistory: List<Track>) {
                    history.clear()
                    history.addAll(searchHistory)
                    historyAdapter.notifyDataSetChanged()
                }
            })
            hideHistoryView()
        }

        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                makeSearch(binding.input.text.toString())
                hideHistoryView()
                true
            }
            false
        }

        binding.updateBtn.setOnClickListener {
            makeSearch(lastResponse)
            binding.trackRecycler.visibility = View.VISIBLE
            binding.errorPlaceholder.visibility = View.GONE
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.input.setText(savedInstanceState.getString(SEARCH_TEXT))
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showErrorSearch() {
        binding.trackRecycler.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showNothingFound() {
        binding.trackRecycler.visibility = View.GONE
        binding.nothingFindScreen.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun showHistoryView() {
        binding.trackRecycler.visibility = View.GONE
        binding.searchHistory.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun hideHistoryView() {
        binding.trackRecycler.visibility = View.VISIBLE
        binding.searchHistory.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgress() {
        binding.trackRecycler.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.nothingFindScreen.visibility = View.GONE
    }

    private fun showRecycler() {
        binding.trackRecycler.visibility = View.VISIBLE
        binding.searchHistory.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
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