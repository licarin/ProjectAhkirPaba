package com.paba.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import com.paba.project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonBook.setOnClickListener {
            // Show the loader
            val loaderFragment = LoaderFragment()
            loaderFragment.isCancelable = false
            loaderFragment.show(supportFragmentManager, "loader")

            // Simulate a delay before navigating
            Handler(Looper.getMainLooper()).postDelayed({
                // Dismiss the loader
                loaderFragment.dismiss()

                // Navigate to the next activity
                val intent = Intent(this, book_detail::class.java)
                startActivity(intent)
            }, 1500) // Simulate 3-second delay
        }

        binding.btnToSignUp.setOnClickListener {
            startActivity(Intent(this, signUp::class.java))
        }

        binding.btnToLogin.setOnClickListener {
            startActivity(Intent(this, login::class.java))
        }
    }
}