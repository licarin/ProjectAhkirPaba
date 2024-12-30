package com.paba.project

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class guide_detail : AppCompatActivity() {
    lateinit var _autoComplete: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guide_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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