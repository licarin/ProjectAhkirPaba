package com.paba.project

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.Manifest
import android.location.Geocoder
import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class guide_detail : AppCompatActivity(), OnMapReadyCallback {
    lateinit var _autoComplete: AutoCompleteTextView
    lateinit var _mapAutoComplete: AutoCompleteTextView
    private var mGoogleMap: GoogleMap? = null
    private val db = Firebase.firestore
    private var suggestions = mutableListOf<String>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var _address: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guide_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // inisialisasi variable
        var _minusIcon = findViewById<TextView>(R.id.minusIcon)
        var _plusIcon = findViewById<TextView>(R.id.plusIcon)
        var _duration = findViewById<TextView>(R.id.tv_duration_value)
        var _keterangan = findViewById<TextView>(R.id.tv_duration_value1)
        var _tvPrice = findViewById<TextView>(R.id.tvPrice)
        var _btnOrder = findViewById<CardView>(R.id.btnOrder)
        var _etNotes = findViewById<EditText>(R.id.et_notes)
        var email = intent.getStringExtra("email").toString()
        _address = findViewById<TextView>(R.id.tvAddressNow)

        // inisialisasi maps
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener({ task ->
                if (task.isSuccessful && task.result != null) {
                    val location: Location = task.result
                    val address = getAddressFromLocation(location.latitude, location.longitude)
                    _address.setText(address)
                    Log.d(
                        "CurrentDebug",
                        "Current Location: Lat: ${location.latitude}, Lng: ${location.longitude}"
                    )
                    val latLng = LatLng(location.latitude, location.longitude)
                    mGoogleMap?.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Marker at current location")
                    )
                    // Move camera to the current location with zoom level 15
                    mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
                } else {
                    Log.d("CurrentDebug", "Failed to get location")
                }
            })
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                42
            )
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.gmaps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // set on click listener
        _minusIcon.setOnClickListener {
            var duration = _duration.text.toString().toInt()
            var tvPrice = _tvPrice.text.toString().toDouble()
            if (duration > 1) {
                duration -= 1
                tvPrice = tvPrice / 2
                _duration.setText(duration.toString())
                _tvPrice.setText(tvPrice.toInt().toString())
                if (duration == 1) {
                    _keterangan.setText("Hour")
                } else {
                    _keterangan.setText("Hours")
                }
            }
        }

        _plusIcon.setOnClickListener {
            var duration = _duration.text.toString().toInt()
            var tvPrice = _tvPrice.text.toString().toDouble()
            duration += 1
            tvPrice = tvPrice * 2
            _duration.setText(duration.toString())
            _keterangan.setText("Hours")
            _tvPrice.setText(tvPrice.toInt().toString())
        }

        // order
        var random = (0..1000).random()
        _btnOrder.setOnClickListener {
            var dataBaru = guideNowOrders(
                id = random,
                location = _mapAutoComplete.text.toString(),
                price = _tvPrice.text.toString().toInt(),
                language = _autoComplete.text.toString(),
                duration = _duration.text.toString().toInt(),
                notes = _etNotes.text.toString()
            )

            db.collection("orders")
                .document(dataBaru.id.toString())
                .set(dataBaru)
                .addOnSuccessListener { documentReference ->
                    Log.d("firebase", "Data berhasil ditambahkan dengan ID: ${dataBaru.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("firebase", "Error adding document", e)
                }

            var intent = Intent(this, book_payment::class.java)
            intent.putExtra("SEARCH_LOCATION", dataBaru.location.toString())
            intent.putExtra("PRICE", dataBaru.price.toDouble())
            intent.putExtra("LANGUAGE", dataBaru.language.toString())
            intent.putExtra("DURATION_VALUE", dataBaru.duration.toString())
            intent.putExtra("NOTES2", dataBaru.notes.toString())
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // auto complete language
        _autoComplete = findViewById<AutoCompleteTextView>(R.id.tv_languange_value)
        val language = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu, language)
        _autoComplete.setAdapter(adapter)

        // auto complete location
        _mapAutoComplete = findViewById<AutoCompleteTextView>(R.id.searchLocation)
        val locationAdapter = ArrayAdapter(this, R.layout.dropdown_menu, suggestions)
        _mapAutoComplete.setAdapter(locationAdapter)

        // text watcher
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("AutoCompleteDebug", "onTextChanged triggered with input: $s")
                if (s.isNullOrEmpty()) {
                    suggestions.clear()
                    locationAdapter.clear()
                    locationAdapter.notifyDataSetChanged()
                    Log.d("AutoCompleteDebug", "Query is empty. Suggestions cleared.")
                    return
                }
                fetchSuggestions(s.toString(), locationAdapter)
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && s.length > 5) {
                    Log.d("AfterTextChangedDebug", "Triggering fetch for: ${s.toString()}")
                    fetchCoordinatesFromFirebase(s.toString()) { latLng ->
                        latLng?.let { location ->
                            Log.d("AfterTextChangedDebug", "Coordinates received: $location")
                            if (mGoogleMap != null) {
                                runOnUiThread {
                                    Log.d(
                                        "AfterTextChangedDebug",
                                        "Updating map with location: $location"
                                    )
                                    mGoogleMap?.clear()
                                    mGoogleMap?.addMarker(
                                        MarkerOptions()
                                            .position(location)
                                            .title("Marker at ${s.toString()}")
                                    )
                                    mGoogleMap?.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            location,
                                            15.0f
                                        )
                                    )
                                }
                            } else {
                                Log.d("MapDebug", "GoogleMap is not ready yet.")
                            }
                        } ?: run {
                            Log.d(
                                "LocationSearch",
                                "Address not found or invalid for: ${s.toString()}"
                            )
                        }
                    }
                }
            }
        }

        // update AutoCompleteTextView text on suggestion selection
        _mapAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selectedSuggestion = parent.getItemAtPosition(position) as String
            _mapAutoComplete.removeTextChangedListener(textWatcher)

            _mapAutoComplete.setText(selectedSuggestion)
            _mapAutoComplete.dismissDropDown()

            // reattach the TextWatcher
            _mapAutoComplete.addTextChangedListener(textWatcher)

            Log.d("AutoCompleteDebug", "Suggestion selected: $selectedSuggestion")
        }

        // attach the TextWatcher
        _mapAutoComplete.addTextChangedListener(textWatcher)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        Log.d("MapDebug", "GoogleMap is ready.")
    }

    private fun fetchCoordinatesFromFirebase(address: String, callback: (LatLng?) -> Unit) {
        val dbRef = db.collection("addresses")
        dbRef.document(address).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val lat = document.getDouble("latitude")
                val lng = document.getDouble("longitude")

                if (lat != null && lng != null) {
                    callback(LatLng(lat, lng))
                } else {
                    callback(null)
                }
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            Log.d("FirebaseError", "Error fetching data")
            callback(null)
        }
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            "Alamat tidak ditemukan"
        }
    }

    private fun fetchSuggestions(query: String, adapter: ArrayAdapter<String>) {
        val queryFields = listOf("name", "city", "province", "state")
        val tempSuggestions = mutableSetOf<String>()

        val queriesDone = queryFields.size
        var processedQueries = 0

        queryFields.forEach { field ->
            db.collection("addresses")
                .whereGreaterThanOrEqualTo(field, query)
                .whereLessThanOrEqualTo(field, query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.getString(field)?.let { tempSuggestions.add(it) }
                    }
                    processedQueries++
                    if (processedQueries == queriesDone) {
                        suggestions.clear()
                        suggestions.addAll(tempSuggestions)

                        // update adapter
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