package com.example.quizzgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.quizzgame.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    lateinit var signUpBinding: ActivitySignUpBinding
    //Create an Object of FirebaseAuth
    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root
        setContentView(view)
        //Hide actionbar
        supportActionBar?.hide()

        signUpBinding.btnSignUp.setOnClickListener {

            val email = signUpBinding.editTextSignUpEmail.text.toString()
            val password = signUpBinding.editTextSignUpPassword.text.toString()
            if(checkField()){
                signUpWithFirebase(email,password)
            }
        }
    }

    fun signUpWithFirebase(email: String, password: String){
        signUpBinding.progressBarSignUp.visibility = View.VISIBLE
        signUpBinding.btnSignUp.isClickable = false

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(applicationContext, "Your account has been created",Toast.LENGTH_LONG).show()
                    finish()
                    signUpBinding.progressBarSignUp.visibility = View.INVISIBLE
                    signUpBinding.btnSignUp.isClickable = true
                } else {
                    // If sign in fails, display a message to the user.
                    //Show the reason for the error
                    Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_LONG).show()
                    signUpBinding.progressBarSignUp.visibility = View.INVISIBLE
                    signUpBinding.btnSignUp.isClickable = true
                }
            }
    }

    private fun checkField(): Boolean {
        val email = signUpBinding.editTextSignUpEmail.text.toString()
        val password = signUpBinding.editTextSignUpPassword.text.toString()
        if(email == ""){
            signUpBinding.textInputLayoutEmail.error = "This is required field"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpBinding.textInputLayoutEmail.error = "Check email format"
            return false
        }
        if (password == ""){
            signUpBinding.editTextSignUpPassword.error = "This field is required"
            return false
        }
        if(password.length <= 7){
            signUpBinding.editTextSignUpPassword.error = "Password at least 8 char"
            return false
        }
        return true
    }
}