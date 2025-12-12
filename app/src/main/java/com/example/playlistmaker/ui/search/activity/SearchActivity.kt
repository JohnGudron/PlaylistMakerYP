package com.example.playlistmaker.ui.search.activity

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.media.MediaActivity
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.TracksState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.google.gson.Gson

const val TRACK = "track"

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private var history = mutableListOf<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private var itemClickAllowed = true

    private lateinit var historyAdapter: TrackAdapter
    private lateinit var sharedPrefs: SharedPreferences

    //private val tracks = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter

    private var searchText = ""
    // TODO save searchText to sharedPrefs
    private var lastResponse = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory()).get(SearchViewModel::class.java)

        viewModel.observeState().observe(this) {
            //renderState(it)
            if (it is TracksState.Content) {
                adapter.tracks.addAll(it.tracks)
                adapter.notifyDataSetChanged()
                hideHistoryView()
                binding.progressBar.visibility = View.GONE
                handler.postDelayed(Runnable { adapter.notifyDataSetChanged() }, 2000)
                if (binding.trackRecycler.visibility == View.VISIBLE) {
                    println()
                }
            }
        }

        viewModel.observeHistory().observe(this) {
            historyAdapter.notifyDataSetChanged()
        }

        sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)

        adapter = TrackAdapter { track -> onItemClick(track) }
        historyAdapter = TrackAdapter { track -> onItemClick(track) }

        binding.input.apply {
            setText(searchText)

            setOnFocusChangeListener { view, hasFocus ->
                viewModel.getSearchHistory()
                if (hasFocus && binding.input.text.isEmpty() && !viewModel.observeHistory().value.isNullOrEmpty()) {
                    showHistoryView()
                } else {
                    hideHistoryView()
                }
            }

            doOnTextChanged { text, _, _, _ ->
                searchText = text.toString()
                clearButtonVisibility(text)
                viewModel.searchDebounce(searchText)
                showProgress()
            }

            doAfterTextChanged { text ->
                searchText = text.toString()
                if (text.isNullOrEmpty()) {
                    showHistoryView()
                }
            }

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.makeSearch(binding.input.text.toString())
                    hideHistoryView()
                    true
                }
                false
            }
        }

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.trackRecycler.adapter = adapter
        binding.trackRecycler.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false )

        //adapter.tracks = tracks

        binding.historyRecycler.apply {
            this.adapter = historyAdapter
            this.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        }
        //historyAdapter.tracks = history

        binding.clearBtn.setOnClickListener {
            searchText = ""
            lastResponse = ""
            binding.input.setText(searchText)
            //tracks.clear()
            adapter.notifyDataSetChanged()
            binding.input.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.input.windowToken, 0)
        }

        binding.clearHistoryBtn.setOnClickListener {
            viewModel.clearSearchHistory()
            hideHistoryView()
        }

        binding.updateBtn.setOnClickListener {
            viewModel.makeSearch(lastResponse)
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

    private fun onItemClick(track: Track) {
        if (itemClickDebounce()) {
            val intent = Intent(this, MediaActivity::class.java)
            intent.putExtra(TRACK, Gson().toJson(track))
            startActivity(intent)
            viewModel.addTrackToHistory(track)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?){
        if (s.isNullOrEmpty()) {
            binding.clearBtn.visibility = View.GONE
        } else {
            binding.clearBtn.visibility = View.VISIBLE
        }
    }

    private fun showErrorSearch() {
        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.VISIBLE
    }

    private fun showNothingFound() {
        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.nothingFindScreen.visibility = View.VISIBLE
    }

    private fun showHistoryView() {
        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchHistory.visibility = View.VISIBLE
    }

    private fun hideHistoryView() {
        binding.trackRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
    }

    private fun showProgress() {
        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchHistory.visibility = View.GONE
        binding.nothingFindScreen.visibility = View.GONE
    }

    private fun showRecycler(tracks: List<Track>) {
        binding.trackRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.searchHistory.visibility = View.GONE
        binding.nothingFindScreen.visibility = View.GONE
        binding.errorPlaceholder.visibility = View.GONE
        binding.clearHistoryBtn.visibility = View.GONE
        binding.historyRecycler.visibility = View.GONE
        //adapter.tracks.clear()
        adapter.tracks = tracks.toMutableList()
        adapter.notifyDataSetChanged()
        historyAdapter.notifyDataSetChanged()
    }

    private fun itemClickDebounce(): Boolean {
        val cur = itemClickAllowed
        if (itemClickAllowed) {
            itemClickAllowed = false
            handler.postDelayed({itemClickAllowed = true}, ITEM_CLICK_DEBOUNCE)
        }
        return cur
    }

    private fun renderState(state: TracksState) {
        when(state) {
            is TracksState.Empty -> showNothingFound()
            is TracksState.Error -> showErrorSearch()
            is TracksState.Content -> showRecycler(state.tracks)
            is TracksState.Loading -> showProgress()
        }
    }

   companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEBOUNCE_DELAY = 1500L
        const val ITEM_CLICK_DEBOUNCE = 1000L
   }
}