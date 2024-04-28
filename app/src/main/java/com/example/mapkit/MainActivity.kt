package com.example.mapkit

import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.traffic.TrafficLayer

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var trafficLayer: TrafficLayer
    private var isKemerovoDisplayed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("ebaaef2d-2b8e-415f-a66d-97dd134028cc")
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)

        // Инициализируем слой трафика
        trafficLayer = MapKitFactory.getInstance().createTrafficLayer(mapView.mapWindow)

        // Включаем отображение слоя трафика
        trafficLayer.setTrafficVisible(false)


        // Включаем обработку отступов краев экрана
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Находим плавающую кнопку и добавляем обработчик нажатия
        val fabLocation = findViewById<FloatingActionButton>(R.id.fab_location)
        fabLocation.setOnClickListener {
            if (isKemerovoDisplayed) {
                moveToKemerovo()
            } else {
                moveToMainPostOffice()
            }
            // Инвертируем флаг для следующего нажатия
            isKemerovoDisplayed = !isKemerovoDisplayed
        }

        // Обработчик для переключателя пробок
        val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                trafficLayer.setTrafficVisible(true)
            } else {
                trafficLayer.setTrafficVisible(false)
            }
        }
    }

    private fun moveToKemerovo() {
        val kemerovoCoordinates = Point(55.355202, 86.086841)
        mapView.map.move(CameraPosition(kemerovoCoordinates, 11.0f, 0.0f, 0.0f))
    }

    private fun moveToMainPostOffice() {
        val mainPostOfficeCoordinates = Point(55.35448, 86.08620)
        mapView.map.move(CameraPosition(mainPostOfficeCoordinates, 17.0f, 0.0f, 0.0f))
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

