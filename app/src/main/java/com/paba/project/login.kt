package com.paba.project

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class login : AppCompatActivity() {

    var db = Firebase.firestore
    lateinit var loginAlertDialog: Dialog

    fun auth (email: String, password: String) {
        db.collection("tbUser")
            .document(email)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val data = result.data
                    if (data?.get("password") == password) {
                        startActivity(Intent(this, home::class.java))
                    } else {
                        loginAlertDialog.show()
                        Log.d("Firebase", "Password salah atau email salah")
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loginAlertDialog = Dialog(this)
        loginAlertDialog.setContentView(R.layout.login_notif_alert)
        loginAlertDialog.window?.setBackgroundDrawableResource(R.drawable.rounded_box)
        loginAlertDialog.setCancelable(false)

        var _btnOk = loginAlertDialog.findViewById<Button>(R.id.btnAlert)
        _btnOk.setOnClickListener {
            loginAlertDialog.dismiss()
        }


        val _etEmail = findViewById<EditText>(R.id.etLEmail)
        val _etPassword = findViewById<EditText>(R.id.etLPasword)

        val _btnLogin = findViewById<Button>(R.id.btnLogin)


        _btnLogin.setOnClickListener {
            val _etnemail = _etEmail.text.toString()
            val _etpassword = _etPassword.text.toString()

            if (_etnemail.isNotEmpty() && _etpassword.isNotEmpty()) {
                auth(_etnemail, _etpassword)
            }
        }

    }
}