package com.paba.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _btnBook = findViewById<Button>(R.id.buttonBook)
        _btnBook.setOnClickListener {
            startActivity(Intent(this, book_detail::class.java))
        }

        val _btn = findViewById<Button>(R.id.btnToSignUp)
        _btn.setOnClickListener {
            startActivity(Intent(this, signUp::class.java))
        }
        val _btnLogin = findViewById<Button>(R.id.btnToLogin)
        _btnLogin.setOnClickListener {
            startActivity(Intent(this, login::class.java))
        }
    }
}