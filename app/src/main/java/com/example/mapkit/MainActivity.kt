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
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.MapObjectCollection

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var trafficLayer: TrafficLayer
    private lateinit var userLocationLayer: UserLocationLayer
    private var isKemerovoDisplayed = true
    private lateinit var mapObjectCollection: MapObjectCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("ebaaef2d-2b8e-415f-a66d-97dd134028cc")
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)

        // Проверяем разрешения
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Если разрешения не выданы, запрашиваем их
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
        }

        // Инициализируем слой трафика
        trafficLayer = MapKitFactory.getInstance().createTrafficLayer(mapView.mapWindow)

        // Инициализируем слой местоположения пользователя
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)

        // Инициализируем коллекцию для объектов на карте
        mapObjectCollection = mapView.map.mapObjects.addCollection()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fabLocation = findViewById<FloatingActionButton>(R.id.fab_location)
        fabLocation.setOnClickListener {
            if (isKemerovoDisplayed) {
                moveToKemerovo()
            } else {
                // Перемещаем карту к текущему местоположению пользователя
                val currentLocation = Point(55.359287, 86.172043)
                mapView.map.move(CameraPosition(currentLocation, 16.0f, 0.0f, 0.0f))

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

        // Добавляем обработчик нажатия на карту
        mapView.map.addInputListener(object : InputListener {
            override fun onMapTap(map: com.yandex.mapkit.map.Map, point: Point) {
                // Вызываем метод добавления точки на карту при нажатии на карту
                addPlacemark(point)
            }

            override fun onMapLongTap(map: com.yandex.mapkit.map.Map, point: Point) {
                // Здесь можно добавить логику для долгого нажатия на карту, если необходимо
            }
        })
    }

    // Метод, вызываемый при нажатии на объект на карте
    private fun onMapObjectTap(mapObject: PlacemarkMapObject, point: Point): Boolean {
        // Добавляем маркер на карту в месте нажатия
        addPlacemark(point)
        // Возвращаем true, чтобы предотвратить передачу события дальше
        return true
    }

    private fun addPlacemark(point: Point) {
        // Создаем маркер
        val placemark = mapObjectCollection.addPlacemark(point)
        // Добавляем обработчик нажатия на маркер
        placemark.addTapListener { _, _ ->
            // Вызываем метод для обработки нажатия на объект на карте
            onMapObjectTap(placemark, point)
        }
    }

    private fun moveToKemerovo() {
        val kemerovoCoordinates = Point(55.355202, 86.086841)
        mapView.map.move(CameraPosition(kemerovoCoordinates, 11.0f, 0.0f, 0.0f))
        // уровень масштабирования карты
        // угол поворота и угол наклона камеры
    }

    // Инициализация и запуск компонентов карты при входе в активность.
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    // Остановка работы с картой и освобождение ресурсов при выходе из активности.
    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
