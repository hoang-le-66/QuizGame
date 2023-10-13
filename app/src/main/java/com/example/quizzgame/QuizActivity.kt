package com.example.quizzgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quizzgame.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding
    //Create an object of FirebaseDatabase
    val database = FirebaseDatabase.getInstance()
    //Set the DB reference object to reach the child questions
    val databaseReference = database.reference.child("questions")
    //Container to retrieve the data
    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0
    var questionNumber = 0

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    lateinit var timer : CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var leftTime = totalTime

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    //Database reference
    val scoreRef = database.reference
    //Create an array
    val questions = HashSet<Int>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root

        setContentView(view)
        //Hide actionbar
        supportActionBar?.hide()
        do{
            //1 to 10
            val number = Random.nextInt(1,11)
            Log.d("number",number.toString())
            questions.add(number)
        }while (questions.size < 5)

        Log.d("numberOfQuestions",questions.toString())
        gameLogic()

        quizBinding.btnNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }

        quizBinding.btnFinish.setOnClickListener {
            sendScore()
        }

        quizBinding.tvA.setOnClickListener {
            userAnswer = "a"
            pauseTimer()
            if (correctAnswer == userAnswer){
                quizBinding.tvA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.tvCorrect.text = userCorrect.toString()

            }else{
                quizBinding.tvA.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.tvWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()
        }

        quizBinding.tvB.setOnClickListener {
            userAnswer = "b"
            pauseTimer()
            if (correctAnswer == userAnswer){
                quizBinding.tvB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.tvCorrect.text = userCorrect.toString()

            }else{
                quizBinding.tvB.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.tvWrong.text = userWrong.toString()
                findAnswer()

            }
            disableClickableOfOptions()

        }
        quizBinding.tvC.setOnClickListener {
            userAnswer = "c"
            pauseTimer()
            if (correctAnswer == userAnswer){
                quizBinding.tvC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.tvCorrect.text = userCorrect.toString()

            }else{
                quizBinding.tvC.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.tvWrong.text = userWrong.toString()
                findAnswer()

            }
            disableClickableOfOptions()

        }
        quizBinding.tvD.setOnClickListener {
            userAnswer = "d"
            pauseTimer()
            if (correctAnswer == userAnswer){
                quizBinding.tvD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.tvCorrect.text = userCorrect.toString()

            }else{
                quizBinding.tvD.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.tvWrong.text = userWrong.toString()
                findAnswer()

            }
            disableClickableOfOptions()
        }
    }

//    private fun gameLogic(){
//        //This interface has 2 functions
//        databaseReference.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                restoreOptions()
//
//                questionCount = snapshot.childrenCount.toInt()
//                if (questionNumber <= questionCount){
//                    //Receive data through DataSnapshot (Firebase object). First, it refer to "1" then "q" and retrieve value
//                    //Receive JSON data and convert to String
//                    //DB only recognize string value, so you have to cast it
//                    question = snapshot.child(questionNumber.toString()).child("q").value.toString()
//                    answerA = snapshot.child(questionNumber.toString()).child("a").value.toString()
//                    answerB = snapshot.child(questionNumber.toString()).child("b").value.toString()
//                    answerC = snapshot.child(questionNumber.toString()).child("c").value.toString()
//                    answerD = snapshot.child(questionNumber.toString()).child("d").value.toString()
//                    correctAnswer = snapshot.child(questionNumber.toString()).child("answer").value.toString()
//
//                    quizBinding.tvQuestion.text = question
//                    quizBinding.tvA.text = answerA
//                    quizBinding.tvB.text = answerB
//                    quizBinding.tvC.text = answerC
//                    quizBinding.tvD.text = answerD
//                    //After successfully retrieving the data, progress bar should disappear, components should be visible
//                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
//                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
//                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
//                    quizBinding.linearLayoutButton.visibility = View.VISIBLE
//
//                    startTimer()
//                }else{
//                    Toast.makeText(applicationContext,"You answered all of questions",Toast.LENGTH_LONG).show()
//
//                }
//
//                questionNumber++
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//                val dialogMessage = AlertDialog.Builder(this@QuizActivity)
//                dialogMessage.setTitle("Quiz Game")
//                dialogMessage.setMessage("Congratulation!!\n You have answered all the questions. Do you want to see the result?")
//                //Dialog message not be closed when user click on anywhere on the screen
//                dialogMessage.setCancelable(false)
//                //Lambda expression
//                dialogMessage.setPositiveButton("See result"){dialogWindow, position ->
//
//                    sendScore()
//
//                }
//                dialogMessage.setNegativeButton("Play again"){dialogWindow, position ->
//                    val intent = Intent(this@QuizActivity,MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//                dialogMessage.create().show()
//
//            }
//
//        })
//    }
    private fun gameLogic(){
        //This interface has 2 functions
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                restoreOptions()

                questionCount = snapshot.childrenCount.toInt()
                if (questionNumber <= questionCount){
                    //Receive data through DataSnapshot (Firebase object). First, it refer to "1" then "q" and retrieve value
                    //Receive JSON data and convert to String
                    //DB only recognize string value, so you have to cast it
                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.tvQuestion.text = question
                    quizBinding.tvA.text = answerA
                    quizBinding.tvB.text = answerB
                    quizBinding.tvC.text = answerC
                    quizBinding.tvD.text = answerD
                    //After successfully retrieving the data, progress bar should disappear, components should be visible
                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
                    quizBinding.linearLayoutButton.visibility = View.VISIBLE

                    startTimer()
                }else{
                    Toast.makeText(applicationContext,"You answered all of questions",Toast.LENGTH_LONG).show()

                }

                questionNumber++

            }

            override fun onCancelled(error: DatabaseError) {

                val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                dialogMessage.setTitle("Quiz Game")
                dialogMessage.setMessage("Congratulation!!\n You have answered all the questions. Do you want to see the result?")
                //Dialog message not be closed when user click on anywhere on the screen
                dialogMessage.setCancelable(false)
                //Lambda expression
                dialogMessage.setPositiveButton("See result"){dialogWindow, position ->

                    sendScore()

                }
                dialogMessage.setNegativeButton("Play again"){dialogWindow, position ->
                    val intent = Intent(this@QuizActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                dialogMessage.create().show()

            }

        })
    }
    //Mark the correct answer in case user click wrong answer
    private fun findAnswer(){
        when(correctAnswer){
            "a" -> quizBinding.tvA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.tvB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.tvC.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.tvD.setBackgroundColor(Color.GREEN)
        }

    }
    fun disableClickableOfOptions(){
        quizBinding.tvA.isClickable = false
        quizBinding.tvB.isClickable = false
        quizBinding.tvC.isClickable = false
        quizBinding.tvD.isClickable = false
    }
    //When user click on Next button, restore Options
    fun restoreOptions(){

        quizBinding.tvA.setBackgroundColor(Color.WHITE)
        quizBinding.tvB.setBackgroundColor(Color.WHITE)
        quizBinding.tvC.setBackgroundColor(Color.WHITE)
        quizBinding.tvD.setBackgroundColor(Color.WHITE)

        quizBinding.tvA.isClickable = true
        quizBinding.tvB.isClickable = true
        quizBinding.tvC.isClickable = true
        quizBinding.tvD.isClickable = true
    }

    private fun startTimer(){
        //Use keyword object represent the anonymous class
        timer = object: CountDownTimer(leftTime,1000){
            //Represent for remaining time
            override fun onTick(millisUntilFinished: Long) {

                leftTime = millisUntilFinished
                updateCountDownText()
            }
            //Time's up
            override fun onFinish() {
                //User can't choose anything when time's up
                disableClickableOfOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.tvQuestion.text = "Time's up, please go to the next question"
                timerContinue = false
            }
        }.start()
        timerContinue = true
    }

    private fun resetTimer() {

        pauseTimer()
        leftTime = totalTime
        updateCountDownText()

    }
    //display timer
    private fun updateCountDownText() {
        val remainingTime : Int = (leftTime/1000).toInt()
        quizBinding.tvTime.text = remainingTime.toString()

    }

    private fun pauseTimer(){
        timer.cancel()
        timerContinue = false

    }

    fun sendScore(){
        //Check null for user
        user?.let{
            // Get UID code of the user
            //"it" keyword mean that user non null
            val userUID = it.uid
            //Represent for the child hold the score in DB
            scoreRef.child("scores").child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child("scores").child(userUID).child("wrong").setValue(userWrong)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext,"Scores sent to DB successfully",Toast.LENGTH_LONG).show()
                    val intent = Intent(this@QuizActivity,ResultActivity::class.java)
                    startActivity(intent)
                    finish()
                }

        }

    }
}