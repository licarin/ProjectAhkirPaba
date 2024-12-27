package com.paba.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class book_detail : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private val db = Firebase.firestore
    private var suggestions = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.searchLocation)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("AutoCompleteDebug", "onTextChanged triggered with input: $s")
                if (s.isNullOrEmpty()) {
                    suggestions.clear()
                    adapter.clear()
                    adapter.notifyDataSetChanged()
                    Log.d("AutoCompleteDebug", "Query is empty. Suggestions cleared.")
                    return
                }
                fetchSuggestions(s.toString(), adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val _etSUBirth = findViewById<TextInputEditText>(R.id.taskDateField2)

        _etSUBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Tampilkan DatePickerDialog
            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Update EditText dengan tanggal yang dipilih
                    _etSUBirth.setText("$selectedYear-${selectedMonth + 1}-$selectedDay")
                },
                year, month, day
            ).show()
        }

        val plusDurBtn = findViewById<Button>(R.id.btn_plus)
        val minDurBtn = findViewById<Button>(R.id.btn_minus)
        val taskDuration = findViewById<TextView>(R.id.tv_duration_value1)

        plusDurBtn.setOnClickListener {
            val currentDuration = taskDuration.text.toString().toIntOrNull() ?: 0
            taskDuration.setText((currentDuration + 1).toString())
        }
        minDurBtn.setOnClickListener {
            val currentDuration = taskDuration.text.toString().toIntOrNull() ?: 0
            if (currentDuration > 1) {
                taskDuration.setText((currentDuration - 1).toString())
            }
        }

        var location = autoCompleteTextView.text.toString()
        var addressDetailText = findViewById<EditText>(R.id.addressDetail).text.toString()
        var notesText = findViewById<EditText>(R.id.notes).text.toString()
        var taskDateText = findViewById<TextInputEditText>(R.id.taskDateField2).text.toString()
        var durationValueText = findViewById<TextView>(R.id.tv_duration_value1).text.toString()
        var notes2Text = findViewById<EditText>(R.id.editTextTextMultiLine).text.toString()

        val btnBook = findViewById<Button>(R.id.bookNow)
        btnBook.setOnClickListener {
//        semesntara book_payment (nanti ganti)
            val intent = Intent(this, book_payment::class.java).apply {
                putExtra("SEARCH_LOCATION", location)
                putExtra("ADDRESS_DETAIL", addressDetailText)
                putExtra("NOTES", notesText)
                putExtra("TASK_DATE", taskDateText)
                putExtra("DURATION_VALUE", durationValueText)
                putExtra("NOTES2", notes2Text)
            }
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

    private fun fetchSuggestions(query: String, adapter: ArrayAdapter<String>) {
        val queryList = listOf("name", "city", "province", "state")
        suggestions.clear()

        val queriesDone = queryList.size
        var processedQueries = 0

        for (field in queryList) {
            db.collection("addresses")
                .whereGreaterThanOrEqualTo(field, query)
                .whereLessThanOrEqualTo(field, query + '\uf8ff') // For prefix matching
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("FirestoreDebug", "Number of documents fetched: ${documents.size()}")
                    for (document in documents) {
                        val name = document.getString("name")
                        Log.d("FirestoreDebug", "Name value: $name")
                        if (name != null && !suggestions.contains(name)) {
                            suggestions.add(name)
                        }
                    }
                    Log.d("SuggestionsDebug", "Suggestions size: ${suggestions.size}")
                    processedQueries++
                    Log.d("SuggestionsDebug", "processedQueries size: ${processedQueries}")
                    if (processedQueries == queriesDone) {
                        // Only update the adapter after all queries have been processed
                        Log.d("SuggestionsDebug", "Suggestions before clearing: ${suggestions.size}")
                        if(adapter.count != 0) {
                            adapter.clear()
                        }
                        Log.d("SuggestionsDebug", "Suggestions after clearing: ${suggestions.size}")
                        adapter.addAll(suggestions)
                        Log.d("SuggestionsDebug", "Suggestions after addAll: ${suggestions.size}")
                        adapter.notifyDataSetChanged()
                        Log.d("AutoCompleteDebug3", "Adapter items count after update: ${adapter.count}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreDebug", "Error fetching data", exception)
                }
        }
    }
}
