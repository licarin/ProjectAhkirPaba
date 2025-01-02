package com.paba.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class home : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: adapterTG
    private val tourGuideList = ArrayList<tourGuide>()
    var db = Firebase.firestore
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(intent.hasExtra("email")) {
            val emailUser = intent.getStringExtra("email") ?: ""
            db.collection("tbUser")
                .document(emailUser)
                .get()
                .addOnSuccessListener { result ->
                    if (result.exists()) {
                        val data = result.data
                        email = data?.get("email").toString()
                    }
                }
        }

//        search section
        val imageView8 = findViewById<ImageView>(R.id.imageView8)
        imageView8.setOnClickListener {
            val intent = Intent(this, search_tour_guide::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.rvTourGuide)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val _tvSGVA = findViewById<TextView>(R.id.tvSGVA)
        _tvSGVA.setOnClickListener {
            startActivity(Intent(this, search_tour_guide::class.java))
        }

        adapter = adapterTG(tourGuideList)
        recyclerView.adapter = adapter
        fetchTourGuideData()
    }

    private fun fetchTourGuideData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("tbTourGuide")
            .orderBy("jumlahClient", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                tourGuideList.clear()
                for (document in documents) {
                    val name = document.getString("nama") ?: ""
                    val lokasi = document.getString("lokasi") ?: ""
                    val rating = document.getDouble("rating")?.toFloat() ?: 0f
                    val harga = document.getString("harga") ?: ""
                    val reviews = document.getString("reviews") ?: ""
                    val bahasa = document.getString("bahasa") ?: ""
                    val jumlahClient = document.getLong("jumlahClient")?.toInt() ?: 0

                    val aboutMe = document.getString("aboutMe") ?: ""
                    val image = document.getString("image") ?: ""

                    val tourGuide = tourGuide(name, lokasi, rating, harga, reviews, bahasa, jumlahClient, aboutMe, image)
                    tourGuideList.add(tourGuide)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

}