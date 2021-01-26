package com.ashish.firebaseauthkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        sign_in_btn.setOnClickListener {
            val email = sign_in_email.text.toString()
            val pass = sign_in_password.text.toString()
            signin(email, pass)
        }
        sign_in_register_here.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun signin(email: String, pass: String) {
        sign_in_progressBar.visibility = View.VISIBLE

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                        sign_in_progressBar.visibility = View.INVISIBLE
                        finish()
                    } else {
                        Toast.makeText(
                            this, "Authentication failed." + task.exception.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        sign_in_progressBar.visibility = View.INVISIBLE
                    }
                }
        } else {
            Toast.makeText(
                this, "Fill all fields",
                Toast.LENGTH_SHORT
            ).show()
            sign_in_progressBar.visibility = View.INVISIBLE
        }
    }


}