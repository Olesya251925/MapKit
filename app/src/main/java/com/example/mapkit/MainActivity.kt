package com.example.mapkit

import android.app.blob.BlobStoreManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition


class MainActivity : AppCompatActivity() {
    private lateinit var mapView: com.yandex.mapkit.mapview.MapView

    // Глобальная переменная для хранения объекта сессии поиска
    private var searchSession: BlobStoreManager.Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("ebaaef2d-2b8e-415f-a66d-97dd134028cc")
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Находим плавающую кнопку и добавляем обработчик нажатия
        val fabLocation = findViewById<FloatingActionButton>(R.id.fab_location)
        fabLocation.setOnClickListener {
            getKemerovoCoordinates()
        }
    }


    private fun getKemerovoCoordinates() {
        val kemerovoCoordinates = Point(55.355202, 86.086841)

        // Перемещаем камеру на координаты города Кемерово
        mapView.map.move(
            CameraPosition(kemerovoCoordinates, 11.0f, 0.0f, 0.0f)
        )
    }
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

}
