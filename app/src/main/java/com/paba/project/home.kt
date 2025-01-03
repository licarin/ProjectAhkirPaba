package com.paba.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
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
    private lateinit var email: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = intent.getStringExtra("email") ?: ""
        Log.d("email", "email: $email")

//        search section
        val imageView8 = findViewById<ImageView>(R.id.imageView8)
        imageView8.setOnClickListener {
            val intent = Intent(this, search_tour_guide::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

//        tombol book now
        val imageView17 = findViewById<ImageView>(R.id.imageView17)
        imageView17.setOnClickListener {
            val intent = Intent(this, search_tour_guide::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        var imageView18 = findViewById<ImageView>(R.id.imageView18)
        imageView18.setOnClickListener {
            val intent = Intent(this, guide_detail::class.java)
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

        val viewjkt = findViewById<FrameLayout>(R.id.jakartaSection)
        val viewsby = findViewById<FrameLayout>(R.id.surabayaSection)
        val viewbdg = findViewById<FrameLayout>(R.id.bandungSection)
        val viewbali = findViewById<FrameLayout>(R.id.baliSection)
        val viewygk = findViewById<FrameLayout>(R.id.yogyakartaSection)
        val viewmdn = findViewById<FrameLayout>(R.id.medanSection)

        viewjkt.setOnClickListener {
            tekanButtonKota("Jakarta")
        }

        viewsby.setOnClickListener {
            tekanButtonKota("Surabaya")
        }

        viewbdg.setOnClickListener {
            tekanButtonKota("Bandung")
        }

        viewbali.setOnClickListener {
            tekanButtonKota("Bali")
        }

        viewygk.setOnClickListener {
            tekanButtonKota("Yogyakarta")
        }

        viewmdn.setOnClickListener {
            tekanButtonKota("Medan")
        }

    }

    private fun tekanButtonKota(location: String) {
        db.collection("tbTourGuide")
            .whereEqualTo("kota", location)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "No guides found in $location", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("home", "Location: $location")
                    Log.d("home", "Email: $email")
                    val newIntent = Intent(this, search_tour_guide::class.java)
                    newIntent.putExtra("location", location)
                    newIntent.putExtra("email", email)
                    startActivity(newIntent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
                    val lokasi = document.getString("lokasi") + ", " + document.getString("kota") ?: ""
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