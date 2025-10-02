package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.api.ItunesApiService
import com.example.playlistmaker.api.ItunesSearchResponse
import com.example.playlistmaker.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText = ""
    private var lastResponse = ""
    private lateinit var input: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var errorScreen: View
    private lateinit var nothingFindScreen: View
    private lateinit var updateBtn: Button

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApiService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        input = findViewById(R.id.input)
        clearButton = findViewById(R.id.clear_btn)
        backBtn = findViewById(R.id.backBtn)
        recycler = findViewById(R.id.track_recycler)
        errorScreen = findViewById(R.id.error_placeholder)
        nothingFindScreen = findViewById(R.id.nothing_find_screen)
        updateBtn = findViewById(R.id.update_btn)

        input.setText(searchText)
        input.doOnTextChanged { text, _, _, _ -> clearButton.visibility=clearButtonVisibility(text) }
        input.doAfterTextChanged { text -> searchText = text.toString() }

        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        adapter.tracks = tracks

        clearButton.setOnClickListener {
            searchText = ""
            lastResponse = ""
            input.setText(searchText)
            tracks.clear()
            adapter.notifyDataSetChanged()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(input.windowToken, 0)
        }

        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                makeSearch(input.text.toString())
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
    }

    private fun showNothingFound() {
        recycler.visibility = View.GONE
        nothingFindScreen.visibility = View.VISIBLE
    }

    private fun makeSearch(str: String) {
        lastResponse = str
        if (str.isNotEmpty()) {
            itunesService.searchTracks(str).enqueue(object : Callback<ItunesSearchResponse> {
                override fun onResponse(
                    call: Call<ItunesSearchResponse>,
                    response: Response<ItunesSearchResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        } else showNothingFound()

                    } else {
                        showErrorSearch()
                    }
                }

                override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                    showErrorSearch()
                }

            })
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}