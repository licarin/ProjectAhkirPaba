package com.paba.project

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // inisialisasi variable
        var _minusIcon = findViewById<TextView>(R.id.minusIcon)
        var _plusIcon = findViewById<TextView>(R.id.plusIcon)
        var _duration = findViewById<TextView>(R.id.tv_duration_value)
        var _keterangan = findViewById<TextView>(R.id.tv_duration_value1)
        var _tvPrice = findViewById<TextView>(R.id.tvPrice)

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
    }

    override fun onResume() {
        super.onResume()
        _autoComplete = findViewById<AutoCompleteTextView>(R.id.tv_languange_value)
        val languange = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu, languange)
        _autoComplete.setAdapter(adapter)
    }
}