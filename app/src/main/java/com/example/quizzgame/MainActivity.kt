package com.example.quizzgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizzgame.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    //At first, you inflated a welcome layout here so two activity go same lol
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        //Hide actionbar
        supportActionBar?.hide()

        mainBinding.btnStartQuiz.setOnClickListener {

            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)

        }
        mainBinding.btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //For GG account
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build()
            val googleSignInClient = GoogleSignIn.getClient(this,gso)
            googleSignInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext,"Sign out successful",Toast.LENGTH_LONG).show()
                }
            }

            //For email and password
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}