package com.example.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private var score = 0
    private var gameStarter = false

    private lateinit var gameScoreTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var tapMeButton: Button
    private lateinit var countDownTimer: CountDownTimer

    private var initialCountDown: Long = 10000
    private var countDownInterval: Long = 1000
    private var timeView = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is: $score")

        //к переменным привязываем представление, находим по id
        //findViewById выполняет поиск в activity_main по id
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeTextView = findViewById(R.id.time_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)

        //setOnClickListener прослушивает нажатие и запускает функцию
        tapMeButton.setOnClickListener { v ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            v.startAnimation(bounceAnimation)
            incrementScore()
        }

        //проверка содержит ли константы значение, если да то получаем значения, затем присваиваем значения и восстанавливаем игру
        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeView = savedInstanceState.getInt(TIME_VIEW_KEY)
            restoreGame()
        }else{
            resetGame()
        }
    }
//для отслежвиание бага при повороте экрана
//функция отслеживает переменные, которые сохраняет при изменении ориентации экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_VIEW_KEY, timeView)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time: $timeView")
    }
//действие уничтожаются когда требуется освободить память
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }
//вызов alert о приложении
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        if(item.itemId == R.id.about){
            showInfo()
        }
        return true
    }

    private fun incrementScore() {
        score++ //увеличиваем переменную
        if(!gameStarter){
            startGame()
        }
        val newScore = getString(R.string.score_view, score)//создаем переменную,присваиваем ей название из файла strings.xml и хранение оценки в виде строки
        gameScoreTextView.text = newScore //берем преставление вызываем метод text и присваеваем newScore
    }

    private fun resetGame() {
        score = 0

        val initialScore = getString(R.string.score_view, score)//создаем переменную,присваиваем ей название из файла strings.xml и хранение оценки в виде строки
        gameScoreTextView.text = initialScore //берем преставление вызываем метод text и присваеваем newScore

        val initialTimeView = getString(R.string.time_view, 10)//создаем переменную,присваиваем ей название из файла string.xml и 10 секунд
        timeTextView.text = initialTimeView

        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeView = millisUntilFinished.toInt() / 1000

                val timeString = getString(R.string.time_view, timeView)
                timeTextView.text = timeString
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarter = false
    }

    private fun restoreGame(){
        val restoredScore = getString(R.string.score_view, score)
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_view, timeView)
        timeTextView.text = restoredTime

        countDownTimer = object: CountDownTimer((timeView * 1000).toLong(), countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeView = millisUntilFinished.toInt() / 1000

                val timeViewString = getString(R.string.time_view, timeView)
                timeTextView.text = timeViewString
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStarter = true
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarter = true
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_over_message, score),
        Toast.LENGTH_LONG).show()//показывать предупреждение длительное время
        resetGame()
    }

    private fun showInfo(){
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

//создает строковые константы для отслеживание
    companion object{
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_VIEW_KEY = "TIME_VIEW_KEY"
    }
}