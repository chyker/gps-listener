package com.example.lab6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.content.Intent
import android.content.SharedPreferences
import android.view.WindowManager
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var password: EditText
    private lateinit var providedPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        password = findViewById(R.id.editTextPassword)

        if (sharedPreferences.getString("password", "") == ""){
            val editor = sharedPreferences.edit()
            editor.putString("password", "admin")
            editor.apply()
        }
    }

    fun login(view: android.view.View) {
        providedPassword = password.text.toString()

        if (providedPassword != "" && providedPassword == sharedPreferences.getString("password", "")) {
            Toast.makeText(applicationContext, "Вход выполнен", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DBInteract::class.java)
            startActivity(intent)
        }
        else Toast.makeText(applicationContext, "Неверный пароль", Toast.LENGTH_SHORT).show()
    }

    fun exit(view: android.view.View) {
        finish()
    }
}
