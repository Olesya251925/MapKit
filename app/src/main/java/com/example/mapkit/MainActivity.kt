package com.example.mapkit

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.traffic.TrafficLayer
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var trafficLayer: TrafficLayer
    private lateinit var userLocationLayer: UserLocationLayer
    private var isKemerovoDisplayed = true
    private lateinit var mapObjectCollection: MapObjectCollection

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            addPlacemark(point)
        }

        override fun onMapLongTap(map: Map, point: Point) {
            addPlacemark(point)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("ebaaef2d-2b8e-415f-a66d-97dd134028cc")
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)

        // Инициализируем слой трафика
        trafficLayer = MapKitFactory.getInstance().createTrafficLayer(mapView.mapWindow)
        requestLocationPermission()

        // Инициализируем слой местоположения пользователя
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true

        // Инициализируем коллекцию для объектов на карте
        mapObjectCollection = mapView.map.mapObjects.addCollection()

        mapView.map.addInputListener(inputListener)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fabLocation = findViewById<FloatingActionButton>(R.id.fab_location)
        fabLocation.setOnClickListener {
            if (isKemerovoDisplayed) {
                moveToCurrentLocation()
            } else {
                moveToKemerovo()
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

    // Проверяем разрешение
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return
        }
    }

    private fun addPlacemark(point: Point) {

        mapObjectCollection.addPlacemark(point)
    }

    private fun moveToKemerovo() {
        val kemerovoCoordinates = Point(55.355202, 86.086841)
        val cameraPosition = CameraPosition(kemerovoCoordinates, 11.0f, 0.0f, 0.0f)
        val animationDuration = 5.0f
        mapView.map.move(cameraPosition, Animation(Animation.Type.SMOOTH, animationDuration), null)
    }

    private fun moveToCurrentLocation() {
        val currentLocation = Point(55.359287, 86.172043)
        val cameraPosition = CameraPosition(currentLocation, 16.0f, 0.0f, 0.0f)
        val animationDuration = 2.0f
        mapView.map.move(cameraPosition, Animation(Animation.Type.SMOOTH, animationDuration), null)
    }

    // Инициализация и запуск компонентов карты при входе в активность.
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
        moveToKemerovo()
    }

    // Остановка работы с картой и освобождение ресурсов при выходе из активности.
    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
