package com.ashish.firebaseauthkotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ashish.firebaseauthkotlin.StartActivity.Companion.googleSignInClient
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        user = auth.currentUser!!

        display.text = user.uid

        val loggingType = intent.getStringExtra("loggingType")

        signout.setOnClickListener {
            auth.signOut()
            when (loggingType) {
                "fb" -> {
                    LoginManager.getInstance().logOut()   ///for facebook logout
                }
                "google" -> {
                    googleSignInClient.signOut()
                }
            }
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
    }
}