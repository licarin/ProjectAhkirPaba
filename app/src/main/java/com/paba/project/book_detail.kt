package com.paba.project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
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

        val imageView = findViewById<ImageView>(R.id.guideImage)
        val guideName = findViewById<TextView>(R.id.guideName)
        val guideLocation = findViewById<TextView>(R.id.guideLocation)
        val guideLanguages = findViewById<TextView>(R.id.guideLanguages)


        val name = intent.getStringExtra("name")
        var harga : Double? = 0.0

        if (name != null) {
            db.collection("tbTourGuide").document(name)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Handle the data
                        val data = document.data

                        val location = document.getString("lokasi") ?: "N/A"
                        val price = document.getString("harga") ?: "N/A"
                        val languages = document.getString("bahasa") ?: "N/A"
                        val image = document.getString("image") ?: "N/A"
                        val nama = document.getString("nama") ?: "N/A"


                        // Example: Display the data (Update your UI elements here)
                        val imageResId = resources.getIdentifier(image, "drawable", packageName)
                        if (imageResId != 0) {
                            imageView.setImageResource(imageResId)
                        }
                        guideName.text = nama
                        guideLocation.text = location
                        guideLanguages.text = languages
                        harga = price.toDouble()
                    } else {
                        Toast.makeText(this, "No such document found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                    Toast.makeText(this, "Failed to fetch data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        val addressesRef = db.collection("addresses")

        addressesRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        // Add or update the 'price' field
                        document.reference.update("price", 100000) // Replace 100 with your desired value
                            .addOnSuccessListener {
                                println("Successfully updated document: ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                println("Error updating document: ${e.message}")
                            }
                    }
                } else {
                    println("No documents found in the collection.")
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching documents: ${e.message}")
            }

        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.searchLocation)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        autoCompleteTextView.setAdapter(adapter)

        // Define the TextWatcher separately for better control
        val textWatcher = object : TextWatcher {
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

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && s.length > 5) {
                    Log.d("AfterTextChangedDebug", "Triggering fetch for: ${s.toString()}")
                    fetchCoordinatesFromFirebase(s.toString()) { latLng ->
                        latLng?.let { location ->
                            Log.d("AfterTextChangedDebug", "Coordinates received: $location")
                            if (mGoogleMap != null) {
                                runOnUiThread {
                                    Log.d("AfterTextChangedDebug", "Updating map with location: $location")
                                    mGoogleMap?.clear()
                                    mGoogleMap?.addMarker(
                                        MarkerOptions()
                                            .position(location)
                                            .title("Marker at ${s.toString()}")
                                    )
                                    mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
                                }
                            } else {
                                Log.d("MapDebug", "GoogleMap is not ready yet.")
                            }
                        } ?: run {
                            Log.d("LocationSearch", "Address not found or invalid for: ${s.toString()}")
                        }
                    }
                }
            }
        }

        // Update AutoCompleteTextView text on suggestion selection
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedSuggestion = parent.getItemAtPosition(position) as String

            // Temporarily remove TextWatcher to avoid triggering it
            autoCompleteTextView.removeTextChangedListener(textWatcher)

            autoCompleteTextView.setText(selectedSuggestion)
            autoCompleteTextView.dismissDropDown()

            // Reattach the TextWatcher
            autoCompleteTextView.addTextChangedListener(textWatcher)

            Log.d("AutoCompleteDebug", "Suggestion selected: $selectedSuggestion")
        }

// Attach the TextWatcher
        autoCompleteTextView.addTextChangedListener(textWatcher)

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

        var taskDurValue = taskDuration.text.toString().toIntOrNull() ?: 1

        plusDurBtn.setOnClickListener {
            val currentDuration = taskDurValue ?: 0
            taskDuration.setText((currentDuration + 1).toString())
            taskDurValue++
        }
        minDurBtn.setOnClickListener {
            val currentDuration = taskDurValue ?: 0
            if (currentDuration > 1) {
                taskDuration.setText((currentDuration - 1).toString())
                taskDurValue++
            }
        }

        val btnBook = findViewById<Button>(R.id.bookNow)
        btnBook.setOnClickListener {
            val loaderFragment = LoaderFragment()
            loaderFragment.isCancelable = false
            loaderFragment.show(supportFragmentManager, "loader")

            // Simulate a delay before navigating
            Handler(Looper.getMainLooper()).postDelayed({
                // Dismiss the loader
                loaderFragment.dismiss()

                // Navigate to the next activity
                val intent = Intent(this, book_payment::class.java).apply {
                    putExtra("SEARCH_LOCATION", autoCompleteTextView.text.toString())
                    putExtra("ADDRESS_DETAIL", findViewById<EditText>(R.id.addressDetail).text.toString())
                    putExtra("NOTES", findViewById<EditText>(R.id.notes).text.toString())
                    putExtra("TASK_DATE", findViewById<TextInputEditText>(R.id.taskDateField2).text.toString())
                    putExtra("DURATION_VALUE", taskDurValue.toString())
                    putExtra("NOTES2", findViewById<EditText>(R.id.editTextTextMultiLine).text.toString())
                    putExtra("PRICE", harga)
                }
                startActivity(intent)
            }, 1500)
        }
    }

    private fun fetchCoordinatesFromFirebase(address: String, callback: (LatLng?) -> Unit) {
        // This function should query Firebase or any other service to fetch the coordinates.
        // For example, using Firebase Realtime Database or Firestore:

        val dbRef = db.collection("addresses")

// Query the specific address document
        dbRef.document(address).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val lat = document.getDouble("latitude")
                val lng = document.getDouble("longitude")

                if (lat != null && lng != null) {
                    callback(LatLng(lat, lng)) // Return the coordinates
                } else {
                    callback(null) // Coordinates not found
                }
            } else {
                callback(null) // Address not found
            }
        }.addOnFailureListener {
            Log.d("FirebaseError", "Error fetching data")
            callback(null)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        Log.d("MapDebug", "GoogleMap is ready.")
    }

    private fun fetchSuggestions(query: String, adapter: ArrayAdapter<String>) {
        val queryFields = listOf("name", "city", "province", "state")
        val tempSuggestions = mutableSetOf<String>() // Use Set to ensure unique entries

        val queriesDone = queryFields.size
        var processedQueries = 0

        queryFields.forEach { field ->
            db.collection("addresses")
                .whereGreaterThanOrEqualTo(field, query)
                .whereLessThanOrEqualTo(field, query + '\uf8ff') // For prefix matching
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.getString(field)?.let { tempSuggestions.add(it) }
                    }
                    processedQueries++
                    if (processedQueries == queriesDone) {
                        // Update suggestions only after all queries are processed
                        suggestions.clear()
                        suggestions.addAll(tempSuggestions)

                        // Update adapter
                        adapter.clear()
                        adapter.addAll(suggestions)
                        adapter.notifyDataSetChanged()

                        Log.d("AutoCompleteDebug", "Suggestions updated: $suggestions")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreDebug", "Error fetching data for field $field", exception)
                }
        }
    }
}
