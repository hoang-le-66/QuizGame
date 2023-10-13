package com.example.quizzgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quizzgame.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    val auth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient
    //??
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        //Change GG login button
        val textOfGoogleButton = loginBinding.btnGoogleSignIn.getChildAt(0) as TextView
        //Write the text that you want to appear on the button
        textOfGoogleButton.text = "Continue with Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        //F stand for float
        textOfGoogleButton.textSize = 18F
        //register activity
        registerActivityForGoogleSignIn()

        loginBinding.btnSignIn.setOnClickListener {

            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginEmail.text.toString()

            if(checkField()){
                signInUser(userEmail, userPassword)
            }

        }

        loginBinding.btnGoogleSignIn.setOnClickListener {

            signInGoogle()

        }

        loginBinding.tvSignUp.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)

        }

    }

    private fun signInGoogle() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("642646596160-3gmn8r9jrnm27nmlkn53lknp3ntqj68t.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
        //Call signIn() function
        signIn()

    }

    private fun signIn() {
        val signInIntent : Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }

    private fun registerActivityForGoogleSignIn(){
        //1st: Activity result contract
        //2nd: Callback: get the return with this parameter
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val data = result.data

                if(resultCode == RESULT_OK && data!= null){
                    //Receive data through this data object
                    val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)

                }

            })

    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        //1st: Account ID token - this ID is unique code for each device
        //2nd: null
        val authCredential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(authCredential).addOnCompleteListener {task ->

            if (task.isSuccessful){


            }else{

            }

        }

    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        //Create an account object from the GG sign in account class
        try{
            //Using GG API for sign in process
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_LONG).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
            //This function take the account object as parameter
            firebaseGoogleAccount(account)
        }catch (e : ApiException){
            Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }
    //By email and password
    fun signInUser(userEmail: String, userPassword: String){
        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task ->
            if(task.isSuccessful){

                Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_LONG).show()
                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()

            }else{

                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_LONG).show()

            }

        }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if(user != null){
            Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_LONG).show()
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkField(): Boolean {
        val userEmail = loginBinding.editTextLoginEmail.text.toString()
        val userPassword = loginBinding.editTextLoginEmail.text.toString()
        if(userEmail == ""){
            loginBinding.editTextLoginEmail.error = "This is required field"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            loginBinding.editTextLoginEmail.error = "Check email format"
            return false
        }
        if (userPassword == ""){
            loginBinding.editTextLoginPassword.error = "This field is required"
            return false
        }
        if(userPassword.length <= 7){
            loginBinding.editTextLoginPassword.error = "Password at least 8 char"
            return false
        }
        return true
    }

}