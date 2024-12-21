package com.paba.project

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class signUp : AppCompatActivity() {
    var db = Firebase.firestore

    val DataUser = ArrayList<user>()

    var data: MutableList<Map<String, String>> = ArrayList()

    lateinit var _etSUEmail : EditText
    lateinit var _etSUPhone : EditText
    lateinit var _etSUPassword : EditText
    lateinit var _etSUName : EditText
    lateinit var _etSUBirth : EditText
    lateinit var _etSUNationality : EditText


    fun TambahData(db: FirebaseFirestore,
                   email: String,
                   phone: String,
                   password: String,
                   name: String,
                   birth: String,
                   nationality: String) {
        val dataBaru = user(email, phone, password, name, birth, nationality)
        db.collection("tbUser")
            .document(dataBaru.email)
            .set(dataBaru)
            .addOnSuccessListener {
                Log.d("Firebase", "Data berhasil ditambahkan")
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        _etSUEmail = findViewById(R.id.etSUEmail)
        _etSUPhone = findViewById(R.id.etSUPhoneNumber)
        _etSUPassword = findViewById(R.id.etSUPassword)
        _etSUName = findViewById(R.id.etSUName)
        _etSUBirth = findViewById(R.id.etSUBirthDate)
        _etSUNationality = findViewById(R.id.etSUNationality)

        val _btnSignUp = findViewById<Button>(R.id.btnSignUp)

        _btnSignUp.setOnClickListener {
            TambahData(
                db,
                _etSUEmail.text.toString(),
                _etSUPhone.text.toString(),
                _etSUPassword.text.toString(),
                _etSUName.text.toString(),
                _etSUBirth.text.toString(),
                _etSUNationality.text.toString()
            )
            finish()
        }
    }
}