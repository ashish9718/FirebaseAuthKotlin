package com.ashish.firebaseauthkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        sign_up_btn.setOnClickListener {
            val email = sign_up_email.text.toString()
            val pass = sign_up_password.text.toString()
            signup(email, pass)
        }
        sign_in_page.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun signup(email: String, pass: String) {
        sign_up_progressBar.visibility = View.VISIBLE

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                        sign_up_progressBar.visibility = View.INVISIBLE
                        finish()
                    } else {
                        Toast.makeText(
                            this, "Authentication failed."+ task.exception.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        sign_up_progressBar.visibility = View.INVISIBLE
                    }
                }
        } else {
            sign_up_progressBar.visibility = View.INVISIBLE
            Toast.makeText(
                this, "Fill all fields",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}