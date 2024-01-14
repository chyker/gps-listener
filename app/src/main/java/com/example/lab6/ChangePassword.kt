package com.example.lab6

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangePassword : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)

        sharedPreferences = getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        oldPassword = findViewById(R.id.etOldPwd)
        newPassword = findViewById(R.id.etNewPwd)
    }

    fun changePassword (view: android.view.View) {
        if (oldPassword.text.toString() == "" || newPassword.text.toString() == "") {
            Toast.makeText(applicationContext, "Заполнены не все поля", Toast.LENGTH_SHORT).show()
        }
        else if (oldPassword.text.toString() != sharedPreferences.getString("password", "")) {
            Toast.makeText(applicationContext, "Неверный пароль", Toast.LENGTH_SHORT).show()
        }
        else if (oldPassword.text.toString() == newPassword.text.toString()){
            Toast.makeText(applicationContext, "Текущий и новый пароли не должны совпадать", Toast.LENGTH_SHORT).show()
        }
        else {
            val editor = sharedPreferences.edit()
            editor.putString("password", newPassword.text.toString())
            editor.apply()
            Toast.makeText(applicationContext, "Пароль изменен", Toast.LENGTH_SHORT).show()
            exit(view)
        }

        // Очищаем поля TextEdit
        oldPassword.setText("")
        newPassword.setText("")
    }

    fun exit(view: android.view.View) {
        finish()
    }
}