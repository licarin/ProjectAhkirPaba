package com.paba.project

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class tourGuide_profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tour_guide_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("name")
        val location = intent.getStringExtra("location")
        val city = intent.getStringExtra("city")
        val language = intent.getStringExtra("language")
        val price = intent.getStringExtra("price")
        val rating = intent.getStringExtra("rating")
        val reviews = intent.getStringExtra("reviews")
        val profilePic = intent.getStringExtra("profile_pic")
        val aboutMe = intent.getStringExtra("aboutMe")

        val tvTGName = findViewById<TextView>(R.id.tvTGName)
        val tvTGRating = findViewById<TextView>(R.id.tvTGRating)
        val tvTGLocation = findViewById<TextView>(R.id.tvTGLocation)
        val tvTGLang = findViewById<TextView>(R.id.tvTGLang)
        val tvPricePerHour = findViewById<TextView>(R.id.tvPricePerHour)
        val tvClientsServed = findViewById<TextView>(R.id.tvClientsServed)
        val tvTGAbout = findViewById<TextView>(R.id.tvTGAbout)
        val ivProfilePic = findViewById<ImageView>(R.id.ivProfilePicture)
        val ivBack = findViewById<ImageView>(R.id.ivBackTGProfile)
        val btnBook = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_TGBook)

        ivBack.setOnClickListener {
            val intent = Intent(this, search_tour_guide::class.java)
            intent.putExtra("location", location) // Pass location back
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Optional: Close current activity
        }

        btnBook.setOnClickListener {
            val loaderFragment = LoaderFragment()
            loaderFragment.isCancelable = false
            loaderFragment.show(supportFragmentManager, "loader")

            // Simulate a delay before navigating
            Handler(Looper.getMainLooper()).postDelayed({
                // Dismiss the loader
                loaderFragment.dismiss()
                val email = intent.getStringExtra("email")
                val intent = Intent(this, book_detail::class.java)
                Log.d("name", "name: $name")
                Log.d("email", "email: $email")
                if (email != null) {
                    intent.putExtra("name", name)
                    intent.putExtra("email", email)
                    intent.putExtra("location", location)
                    intent.putExtra("city", city)
                    intent.putExtra("language", language)
                    intent.putExtra("price", price)
                    intent.putExtra("rating", rating)
                    intent.putExtra("reviews", reviews)
                    intent.putExtra("profile_pic", profilePic)
                    intent.putExtra("aboutMe", aboutMe)
                }
                startActivity(intent)

            }, 1500) // Simulate 3-second delay
        }

        tvTGName.text = name
        tvTGRating.text = rating
        tvTGLocation.text = "$location, $city"
        tvTGLang.text = language
        tvPricePerHour.text = price
        tvClientsServed.text = reviews
        tvTGAbout.text = aboutMe

        val imageResId = resources.getIdentifier(profilePic, "drawable", packageName)
        if (imageResId != 0) {
            ivProfilePic.setImageResource(imageResId)
        }
    }

}