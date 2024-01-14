package com.example.lab6

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import java.text.SimpleDateFormat


class DBInteract : AppCompatActivity(), LocationListener {
    private val locationPermissionCode = 2

    private lateinit var locationManager: LocationManager
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvBearing: TextView
    private lateinit var btnGPS: Button
    private lateinit var btnWrite: Button
    private lateinit var tvOutput: TextView
    private lateinit var lastUpdateTime: String
    private lateinit var lastLatitude: String
    private lateinit var lastLongitude: String
    private lateinit var lastBearing: String
    private lateinit var database: SQLiteDatabase
    private lateinit var dbHelper: DbHelper
    private var isGPSListening: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.db_interact)
        tvOutput = findViewById(R.id.outputTV)
        btnGPS = findViewById(R.id.btnGPS)
        btnWrite = findViewById(R.id.btnWrite)

        dbHelper = DbHelper(applicationContext)
        database = dbHelper.writableDatabase

        // Делаем TextView прокручиваемым
        tvOutput.movementMethod = ScrollingMovementMethod()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // С помощью этого объекта будем получать координаты
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Делаем кнопку "GPS" красной
        ViewCompat.setBackgroundTintList(
            btnGPS,
            ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
        )

        // Затеняем кнопку "Добавить"
        ViewCompat.setBackgroundTintList(
            btnWrite,
            ContextCompat.getColorStateList(this, android.R.color.system_background_dark)
        )

        // Делаем кнопку "Добавить" неактивной
        btnWrite.isEnabled = false
        btnWrite.isClickable = false
    }

    // Затирание базы данных
    fun clear(view: android.view.View) {
        database.delete(DbHelper.TABLE_GEOTAG, null, null)
        Toast.makeText(this, "БД очищена", Toast.LENGTH_SHORT).show()

        read(view)
    }

    // Запись последних полученных координат в базу данных
    fun writeLocation(view: android.view.View) {
        val contentValues = ContentValues().apply {
            put(DbHelper.KEY_TIME, lastUpdateTime)
            put(DbHelper.KEY_LATITUDE, lastLatitude)
            put(DbHelper.KEY_LONGITUDE, lastLongitude)
            put(DbHelper.KEY_BEARING, lastBearing)
        }

        database.insert(DbHelper.TABLE_GEOTAG, null, contentValues)

        read(view)
    }

    // Вывод содержимого базы данных в центральный TextView
    fun read(view: android.view.View) {
        val cursor = database.query(DbHelper.TABLE_GEOTAG, null, null, null, null, null, null)

        cursor?.moveToFirst()

        val stringBuilder = StringBuilder()

        while (cursor?.isAfterLast == false) {
            val time = cursor.getString(cursor.getColumnIndex(DbHelper.KEY_TIME))
            val latitude = cursor.getString(cursor.getColumnIndex(DbHelper.KEY_LATITUDE))
            val longitude = cursor.getString(cursor.getColumnIndex(DbHelper.KEY_LONGITUDE))
            val bearing = cursor.getString(cursor.getColumnIndex(DbHelper.KEY_BEARING))

            stringBuilder.append("$time: $latitude, $longitude, $bearing°\n")
            cursor.moveToNext()
        }

        if (stringBuilder.toString() != "") tvOutput.text = stringBuilder.toString()
        else tvOutput.text = "База данных пуста"

        cursor?.close()
    }

    // Включение и выключение подписики на получение геоданных
    fun switchGPS(view: android.view.View) {
        btnGPS = findViewById(R.id.btnGPS)

        if (!isGPSListening) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Проверка наличия у приложения прав для получения геоданных
            if ((ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                // Интерактивный запрос прав если таковые отсутствуют
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionCode
                )
            }

            // Начать цикл получения GPS данных с минимальынм интервалом 5 секунд и изменением
            // расстояния не менее 5 метров
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                         5000, 5f, this)

            // Делаем кнопку "GPS" зеленой
            ViewCompat.setBackgroundTintList(
                btnGPS,
                ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
            )

            isGPSListening = true

        } else {
            locationManager.removeUpdates(this)

            // Делаем кнопку "GPS" красной
            ViewCompat.setBackgroundTintList(
                btnGPS,
                ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
            )

            isGPSListening = false
        }
    }

    // Данная функция будет динамично выполняться после каждого получения геоданных.
    // Именно для ее перезаписи данный класс унаследован от LocationListener.
    override fun onLocationChanged(location: Location) {
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvBearing = findViewById(R.id.tvBearing)

        lastUpdateTime = SimpleDateFormat("HH:mm:ss").format(location.time)

        lastLatitude =  location.latitude.toString()
        lastLongitude = location.longitude.toString()
        lastBearing = location.bearing.toString()

        tvLatitude.text = "Широта:\t" + lastLatitude
        tvLongitude.text = "Долгота:\t" + lastLongitude
        tvBearing.text = "Азимут:\t" + lastBearing + "°"

        // Включение функционирования кнопки "Записать"
        btnWrite.isEnabled = true
        btnWrite.isClickable = true

        // Отмена затенения кнопки "Записать"
        ViewCompat.setBackgroundTintList(
            btnWrite,
            ContextCompat.getColorStateList(this, android.R.color.transparent)
        )
    }

    // Вызов Activity для изменения пароля
    fun changePassword(view: android.view.View) {
        val intent = Intent(this, ChangePassword::class.java)
        startActivity(intent)
    }

    // Данная функция вызывается при любом изменении прав доступа приложения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Выводит Toast, если были изменены права геолокации
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Доступ предоставлен", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Доступ отклонен", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun exit(view: android.view.View) {
        locationManager.removeUpdates(this)
        dbHelper.close()
        finish()
    }
}