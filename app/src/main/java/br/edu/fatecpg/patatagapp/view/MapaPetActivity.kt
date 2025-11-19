package br.edu.fatecpg.patatagapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.LocationDto
import br.edu.fatecpg.patatagapp.api.PetDto
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.patatag.app.databinding.ActivityMapaPetBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapaPetActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapaPetBinding
    private var mMap: GoogleMap? = null
    private var petId: Int = -1
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 5000 // Atualizar a cada 5 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapaPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val petIdString = intent.getStringExtra("PET_ID")
        if (petIdString != null) {
            petId = petIdString.toInt()
        } else {
            finish()
            return
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupListeners()
        loadPetDetails()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnActionHistory.setOnClickListener {
            Toast.makeText(this, "Histórico em breve", Toast.LENGTH_SHORT).show()
        }
        binding.btnActionGeofence.setOnClickListener {
            Toast.makeText(this, "Cercas em breve", Toast.LENGTH_SHORT).show()
        }
        binding.btnActionSound.setOnClickListener {
            Toast.makeText(this, "Função emitir som em breve", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isZoomControlsEnabled = true

        // Inicia o loop de atualização de localização
        startLocationUpdates()
    }

    private fun loadPetDetails() {
        RetrofitClient.instance.getPetDetails(petId).enqueue(object : Callback<PetDto> {
            override fun onResponse(call: Call<PetDto>, response: Response<PetDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val pet = response.body()!!
                    binding.tvPetName.text = pet.name
                    binding.tvBattery.text = "${pet.battery_level}%"
                    binding.tvLastSeen.text = if (pet.is_online) "Status: Online" else "Status: Offline"

                    // Se tiver localização, já atualiza o mapa
                    pet.last_location?.let { loc ->
                        updateMapLocation(LatLng(loc.latitude, loc.longitude))
                    }
                }
            }
            override fun onFailure(call: Call<PetDto>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro ao carregar pet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            fetchPetLocation()
            handler.postDelayed(this, updateInterval)
        }
    }

    private fun startLocationUpdates() {
        handler.post(updateRunnable)
    }

    private fun stopLocationUpdates() {
        handler.removeCallbacks(updateRunnable)
    }

    private fun fetchPetLocation() {
        RetrofitClient.instance.getPetLocation(petId).enqueue(object : Callback<LocationDto> {
            override fun onResponse(call: Call<LocationDto>, response: Response<LocationDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val loc = response.body()!!
                    updateMapLocation(LatLng(loc.latitude, loc.longitude))
                }
            }
            override fun onFailure(call: Call<LocationDto>, t: Throwable) {
                // Falha silenciosa no loop para não spamar toasts
            }
        })
    }

    private fun updateMapLocation(location: LatLng) {
        mMap?.let { map ->
            map.clear()
            map.addMarker(MarkerOptions().position(location).title("Pet"))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }
}