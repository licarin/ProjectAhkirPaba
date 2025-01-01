package com.paba.project

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import f_main_search

class search_tour_guide : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_tour_guide)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // fragment mainn
        val initialFragment = f_main_search()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, initialFragment)
            .commit()

        // Search bar
        val searchBar = findViewById<EditText>(R.id.searchInput)
        searchBar.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val searchText = searchBar.text.toString().lowercase()

                if (searchText.isNotEmpty()) {
                    val searchFragment = f_guide_search.newInstance(searchText)
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragmentContainer,
                            searchFragment
                        )
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(this, "Please enter a city or guide name", Toast.LENGTH_SHORT)
                        .show()
                }
                true
            } else {
                false
            }
        }

        // Back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val fragment = fragmentManager.findFragmentById(R.id.fragmentContainer)

            if (fragment is f_guide_search) {
                val mainSearchFragment = f_main_search()
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, mainSearchFragment)
                    .commit()
            } else {
                // buat ke home nnti
            }
        }
    }
}