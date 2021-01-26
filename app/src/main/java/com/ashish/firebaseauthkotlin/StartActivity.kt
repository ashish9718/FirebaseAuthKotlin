package com.ashish.firebaseauthkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_start.*
import java.util.*

private const val TAG = "StartActivity"

class StartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"

    private lateinit var loggingType: String

    companion object {
        private const val RC_SIGN_IN = 123
        lateinit var googleSignInClient: GoogleSignInClient
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        auth = Firebase.auth

        emaillogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        phone.setOnClickListener {
            startActivity(Intent(this, PhoneAuthentication::class.java))
        }
        google.setOnClickListener {
            loggingType="google"
            googleSignIn()
        }
        facebook.setOnClickListener {
            loggingType="fb"
            fbLogin()
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    ///// fb SignIn methods

    private fun fbLogin() {
        callbackManager = CallbackManager.Factory.create()
        facebook.setPermissions(listOf(EMAIL))
        facebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(exception: FacebookException) {
                Log.d(TAG, "facebook:onError", exception)
                Toast.makeText(this@StartActivity, exception.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent =Intent(this, MainActivity::class.java)
                    intent.putExtra("loggingType",loggingType)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this, "Authentication failed." + task.exception, Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    ///////


    ///// googleSignIn methods

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(loggingType){
            "fb"->{
                callbackManager.onActivityResult(requestCode, resultCode, data)
            }
            "google"->{
                if (requestCode == RC_SIGN_IN) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val exception = task.exception
                    if (task.isSuccessful) {
                        try {
                            val account = task.getResult(ApiException::class.java)!!
                            firebaseAuthWithGoogle(account.idToken!!)
                        } catch (e: ApiException) {
                            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent =Intent(this, MainActivity::class.java)
                    intent.putExtra("loggingType",loggingType)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }

    ////////

}