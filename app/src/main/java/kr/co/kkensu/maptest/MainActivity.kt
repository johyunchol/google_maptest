package kr.co.kkensu.maptest

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapApi: GoogleMap

    private var polyline: Polyline? = null
    private var circle: Circle? = null
    private var polygon: Polygon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mapApi = googleMap!!

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(37.503481, 127.044964)


        mapApi.addMarker(MarkerOptions().position(sydney).title("선릉"))
        mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17f))

        mapApi.setOnMapClickListener {
            mapApi.addMarker(MarkerOptions().position(it).title("선릉"))
            mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
        }

        initResources()
    }

    private fun initResources() {
        btnPolyline.setOnClickListener {
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()

            val point1 = LatLng(37.499222, 127.065038)
            val point2 = LatLng(37.493910, 127.082934)

            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.parseColor("#FF0000"))
            polylineOptions.width(10f)
            polylineOptions.add(point1)
            polylineOptions.add(point2)

            if (polyline != null) {
                polyline?.remove()
            }
            polyline = mapApi.addPolyline(polylineOptions) // 직선그리기

            val latLngBounds = LatLngBounds.Builder()
            latLngBounds.include(point1)
            latLngBounds.include(point2)

            mapApi.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 17))
        }

        btnCircle.setOnClickListener {
            val center = LatLng(37.499222, 127.065038)

            val circleOptions = CircleOptions()
            circleOptions.strokeColor(Color.parseColor("#00FF00"))
            circleOptions.strokeWidth(10f)
            circleOptions.fillColor(Color.parseColor("#4400FF00"))
            circleOptions.radius(100.0)
            circleOptions.center(center)

            if (circle != null) {
                circle?.remove()
            }
            circle = mapApi.addCircle(circleOptions)
            mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 17f))
        }

        btnPolygon.setOnClickListener {
            val list: MutableList<LatLng> = ArrayList()
            list.add(LatLng(37.503818, 127.064695))
            list.add(LatLng(37.506372, 127.078299))
            list.add(LatLng(37.501656, 127.089006))
            list.add(LatLng(37.494421, 127.082591))
            list.add(LatLng(37.495936, 127.061863))

            val polygonOptions = PolygonOptions()
            polygonOptions
                    .addAll(createOuterBounds())
                    .strokeWidth(10f)
                    .zIndex(1f)
                    .strokeColor(Color.parseColor("#ADADAD"))
                    .addHole(list) //                .addHole(createHole(new LatLng(37.683758, 127.308075), 1000))
                    .fillColor(Color.parseColor("#44000000"))

            if (polygon != null) {
                polygon?.remove()
            }
            polygon = mapApi.addPolygon(polygonOptions)

            val latLngBounds = LatLngBounds.Builder()
            for (latLng in list) {
                latLngBounds.include(latLng)
                latLngBounds.include(latLng)
            }
            mapApi.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 17))
        }
    }

    private fun createOuterBounds(): List<LatLng?>? {
        val delta = 0.01f
        return object : ArrayList<LatLng?>() {
            init {
                add(LatLng((90 - delta).toDouble(), (-180 + delta).toDouble()))
                add(LatLng(0.0, (-180 + delta).toDouble()))
                add(LatLng((-90 + delta).toDouble(), (-180 + delta).toDouble()))
                add(LatLng((-90 + delta).toDouble(), 0.0))
                add(LatLng((-90 + delta).toDouble(), (180 - delta).toDouble()))
                add(LatLng(0.0, (180 - delta).toDouble()))
                add(LatLng((90 - delta).toDouble(), (180 - delta).toDouble()))
                add(LatLng((90 - delta).toDouble(), 0.0))
                add(LatLng((90 - delta).toDouble(), (-180 + delta).toDouble()))
            }
        }
    }

    private fun clearDraw() {
        mapApi.clear()
    }
}