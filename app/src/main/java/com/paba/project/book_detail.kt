package com.paba.project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class book_detail : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap:GoogleMap? = null
    var db = Firebase.firestore
    private val suggestions = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dummyAddresses = listOf(
            address(
                name = "125 Main St",
                city = "New York",
                province = "NY",
                state = "USA"
            ),
            address(
                name = "456 Elm St",
                city = "Los Angeles",
                province = "CA",
                state = "USA"
            ),
            address(
                name = "789 Oak St",
                city = "Chicago",
                province = "IL",
                state = "USA"
            ),
            address(
                name = "102 Maple Ave",
                city = "Houston",
                province = "TX",
                state = "USA"
            ),
            address(
                name = "202 Pine St",
                city = "Phoenix",
                province = "AZ",
                state = "USA"
            )
        )

        // Add each address to the Firestore collection
        for (address in dummyAddresses) {
            db.collection("addresses").document(address.name)
                .set(address)
                .addOnSuccessListener {
                    Log.e("successData","Successfully added: $address.name")
                }
                .addOnFailureListener { e ->
                    Log.e("errorData","Error adding document: $e")
                }
        }

        var autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.searchLocation)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    fetchSuggestions(s.toString(), adapter)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

    private fun fetchSuggestions(query: String, adapter: ArrayAdapter<String>) {
        db.collection("addresses")
            .whereGreaterThanOrEqualTo("address", query)
            .whereLessThanOrEqualTo("name", query + '\uf8ff') // For prefix matching
            .get()
            .addOnSuccessListener { documents ->
                suggestions.clear()
                for (document in documents) {
                    val name = document.getString("name")
                    if (name != null) {
                        suggestions.add(name)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}

