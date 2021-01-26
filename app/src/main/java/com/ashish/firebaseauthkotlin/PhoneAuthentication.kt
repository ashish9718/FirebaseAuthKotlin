package com.ashish.firebaseauthkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_phone_authentication.*
import java.util.concurrent.TimeUnit

class PhoneAuthentication : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var otpid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_authentication)

        mAuth = Firebase.auth

        ccp.registerCarrierNumberEditText(t1)

        b1.setOnClickListener {
            initiateotp()
        }
        b2.setOnClickListener {
            authenticate()
        }
    }

    private fun initiateotp() {
        val phonenumber = ccp.fullNumberWithPlus.replace(" ", "")

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phonenumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(
                object : OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                    otpid = s
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signIn(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            })          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    toast("Logged in Successfully :)")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Signin Code Error", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun authenticate() {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(otpid, t2.text.toString())

        signIn(credential)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}