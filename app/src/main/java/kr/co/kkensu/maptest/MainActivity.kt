package kr.co.kkensu.maptest

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.infowindow.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var activity: Activity

    private lateinit var mapApi: GoogleMap

    private var list: MutableList<Polyline>? = null
    private var polyline: Polyline? = null
    private var circle: Circle? = null
    private var polygon: Polygon? = null

    lateinit var api: ServiceApiImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        setContentView(R.layout.activity_main)
        api = ServiceApiImpl()


        if (BuildConfig.IS_DEBUG) {
            // 디버그 버전일때만 수행
            Log.e("JHC_DEBUG", "test")
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mapApi = googleMap!!

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(37.503481, 127.044964)


        mapApi.addMarker(MarkerOptions().position(sydney).title("선릉"))
        mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17f))

//        mapApi.setOnMapClickListener {
//            mapApi.addMarker(MarkerOptions().position(it).title("선릉"))
//            mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
//        }

        /**
         * 지도를 움직이기 시작할 때 발생하는 이벤트 리스너
         */
        mapApi.setOnCameraMoveStartedListener(object : GoogleMap.OnCameraMoveStartedListener {
            override fun onCameraMoveStarted(p0: Int) {
                Log.e("JHC_DEBUG", "started : " + p0)

                txtCenter.text = ""
                txtCenter.hint = "이동중입니다..."
            }
        })

        /**
         * 지도를 움직이다가 손을 뗐을 때 발생하는 이벤트 리스너
         */
        mapApi.setOnCameraIdleListener(object : GoogleMap.OnCameraIdleListener {
            override fun onCameraIdle() {
                Log.e("JHC_DEBUG", "onCameraIdle")

                txtCenter.text = getCenter().toString()
            }

        })

        mapApi.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(p0: Marker?): View {
                var mInfoView = activity.layoutInflater.inflate(R.layout.infowindow, null)

                mInfoView.txtLatLng.text =
                    String.format("%f/%f", p0?.position?.latitude, p0?.position?.longitude)

                return mInfoView
            }

            override fun getInfoWindow(p0: Marker?): View? {
                return null
            }
        })

        mapApi.setOnInfoWindowClickListener {
            Toast.makeText(activity, it.position.toString(), Toast.LENGTH_LONG).show()
        }

        initResources()

        startLocationService();
    }

    private fun initResources() {
        btnVehicle.setOnClickListener{
            realtimeLocation()
        }

        btnParking.setOnClickListener{
            parkLocation()
        }

        btnReset.setOnClickListener {
            mapApi.clear()
        }

        btnPolyline.setOnClickListener {
            clearDraw()

            val point1 = LatLng(37.499222, 127.065038)
            val point2 = LatLng(37.493910, 127.082934)
            val point3 = LatLng(37.491194, 127.089253)

            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.parseColor("#FF0000"))
            polylineOptions.width(10f)
            polylineOptions.add(point1)
            polylineOptions.add(point2)
            polylineOptions.add(point3)

            polyline = mapApi.addPolyline(polylineOptions) // 직선그리기

            val latLngBounds = LatLngBounds.Builder()
            latLngBounds.include(point1)
            latLngBounds.include(point2)
            latLngBounds.include(point3)

            mapApi.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 0))
        }

        btnCircle.setOnClickListener {
            clearDraw()
            val center = LatLng(37.499222, 127.065038)

            val circleOptions = CircleOptions()
            circleOptions.strokeColor(Color.parseColor("#00FF00"))
            circleOptions.strokeWidth(10f)
            circleOptions.fillColor(Color.parseColor("#4400FF00"))
            circleOptions.radius(100.0)
            circleOptions.center(center)

            circle = mapApi.addCircle(circleOptions)
            mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 17f))
        }

        btnPolygon.setOnClickListener {
            clearDraw()
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
                .addHole(list)
                .fillColor(Color.parseColor("#44000000"))

            polygon = mapApi.addPolygon(polygonOptions)

            val latLngBounds = LatLngBounds.Builder()
            for (latLng in list) {
                latLngBounds.include(latLng)
                latLngBounds.include(latLng)
            }
            mapApi.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 17))
        }

        btnMultiPolygon.setOnClickListener {
            clearDraw()

            val list: MutableList<LatLng> = ArrayList()
            list.add(LatLng(37.503818, 127.064695))
            list.add(LatLng(37.506372, 127.078299))
            list.add(LatLng(37.501656, 127.089006))
            list.add(LatLng(37.494421, 127.082591))
            list.add(LatLng(37.495936, 127.061863))
            list.add(LatLng(37.503818, 127.064695))

            val list2: MutableList<LatLng> = ArrayList()
            list2.add(LatLng(37.499299, 127.025202))
            list2.add(LatLng(37.501358, 127.034922))
            list2.add(LatLng(37.493119, 127.034086))
            list2.add(LatLng(37.492131, 127.028957))
            list2.add(LatLng(37.499299, 127.025202))

            val polygonOptions = PolygonOptions()
            polygonOptions
                .addAll(createOuterBounds())
                .strokeWidth(10f)
                .zIndex(1f)
                .strokeColor(Color.parseColor("#ADADAD"))
                .addHole(list)
                .addHole(list2)
                .fillColor(Color.parseColor("#44000000"))

            polygon = mapApi.addPolygon(polygonOptions)

            val latLngBounds = LatLngBounds.Builder()
            for (latLng in list) {
                latLngBounds.include(latLng)
                latLngBounds.include(latLng)
            }

            for (latLng in list2) {
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
        if (polyline != null) polyline?.remove()
        if (circle != null) circle?.remove()
        if (polygon != null) polygon?.remove()
    }

    fun getCenter(): LatLng? {
        return mapApi.cameraPosition.target
    }

    open fun startLocationService() {
        val intent = Intent(activity, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(intent)
        } else {
            activity.startService(intent)
        }
    }

    private fun realtimeLocation() {
        api.search("ocNIrM4hVmZ8GnUBFFsUjmrWl5j1", "3140").enqueue(object : Callback<GetSearchResponse> {
            override fun onResponse(
                call: Call<GetSearchResponse>,
                response: Response<GetSearchResponse>
            ) {

                var realTimeVehicleStatus = response.body()?.data?.realTimeVehicleStatus

                val lat = realTimeVehicleStatus?.resMsg?.lat!!
                val lon = realTimeVehicleStatus.resMsg?.lon!!

                val location = LatLng(lat, lon)
                mapApi.addMarker(MarkerOptions().position(location).title("선릉"))
                mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))

                Log.e("JHC_DEBUG", String.format("lat : %s / lon : %s", lat.toString(), lon.toString()))
            }

            override fun onFailure(call: Call<GetSearchResponse>, t: Throwable) {
                Log.e("JHC_DEBUG", t?.message)
            }
        })
    }

    private fun parkLocation() {
        api.search("ocNIrM4hVmZ8GnUBFFsUjmrWl5j1", "3140").enqueue(object : Callback<GetSearchResponse> {
            override fun onResponse(
                call: Call<GetSearchResponse>,
                response: Response<GetSearchResponse>
            ) {

                var parkLocation = response.body()?.data?.parkLocation

                val lat = parkLocation?.lat!!
                val lon = parkLocation.lon

                val location = LatLng(lat, lon)
                mapApi.addMarker(MarkerOptions().position(location).title("선릉"))
                mapApi.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))

                Log.e("JHC_DEBUG", String.format("lat : %s / lon : %s", lat.toString(), lon.toString()))
            }

            override fun onFailure(call: Call<GetSearchResponse>, t: Throwable) {
                Log.e("JHC_DEBUG", t?.message)
            }
        })
    }
}