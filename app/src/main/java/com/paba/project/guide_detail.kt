package com.paba.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class guide_detail : AppCompatActivity() {
    lateinit var _autoComplete: AutoCompleteTextView

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

        // inisialisasi database
        val db = Firebase.firestore

        // inisialisasi variable
        var _minusIcon = findViewById<TextView>(R.id.minusIcon)
        var _plusIcon = findViewById<TextView>(R.id.plusIcon)
        var _duration = findViewById<TextView>(R.id.tv_duration_value)
        var _keterangan = findViewById<TextView>(R.id.tv_duration_value1)
        var _tvPrice = findViewById<TextView>(R.id.tvPrice)
        var _btnOrder = findViewById<CardView>(R.id.btnOrder)
        var _etNotes = findViewById<EditText>(R.id.et_notes)

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
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        _autoComplete = findViewById<AutoCompleteTextView>(R.id.tv_languange_value)
        val language = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu, language)
        _autoComplete.setAdapter(adapter)
    }
}