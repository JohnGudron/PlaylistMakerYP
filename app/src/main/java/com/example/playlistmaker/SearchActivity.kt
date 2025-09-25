package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged

class SearchActivity : AppCompatActivity() {

    var searchText = ""
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        input = findViewById(R.id.input)
        input.setText(searchText)
        val clearButton = findViewById<ImageView>(R.id.clear_btn)
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        clearButton.setOnClickListener {
            searchText = ""
            input.setText(searchText)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(input.windowToken, 0)
        }

        input.doOnTextChanged { text, _, _, _ -> clearButton.visibility=clearButtonVisibility(text) }
        input.doAfterTextChanged { text -> searchText = text.toString() }

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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}