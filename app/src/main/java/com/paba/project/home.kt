package com.paba.project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class home : AppCompatActivity() {
    var db = Firebase.firestore
    private lateinit var _nama : ArrayList<String>
    private lateinit var _lokasi : ArrayList<String>
    private lateinit var _rating : ArrayList<String>
    private lateinit var _reviews : ArrayList<String>
    private lateinit var _image : ArrayList<String>

    private var arTG = ArrayList<tourGuide>()
    private lateinit var _rvTG : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        var db = Firebase.firestore
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _rvTG = findViewById(R.id.rvTourGuide)
    }
}