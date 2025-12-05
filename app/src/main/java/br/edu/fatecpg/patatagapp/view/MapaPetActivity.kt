package br.edu.fatecpg.patatagapp

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.*
import br.edu.fatecpg.patatagapp.databinding.ActivityMapaPetBinding
import com.bumptech.glide.Glide
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapaPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapaPetBinding
    private var petId: Int = -1
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 5000
    private var isFirstUpdate = true

    private var isAddingGeofence = false

    // Referências aos overlays para poder limpar depois
    private var petMarker: Marker? = null
    private val geofencePolygons = mutableListOf<Polygon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração do Osmdroid (Essencial para carregar o mapa)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        binding = ActivityMapaPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val petIdExtra = intent.getIntExtra("PET_ID", -1)
        if (petIdExtra != -1) petId = petIdExtra
        else {
            val str = intent.getStringExtra("PET_ID")
            if(str != null) petId = str.toInt() else { finish(); return }
        }

        setupMap()
        setupListeners()
        loadPetDetails()
    }

    private fun setupMap() {
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)

        // Ponto inicial (São Paulo)
        val startPoint = GeoPoint(-23.5505, -46.6333)
        binding.map.controller.setZoom(15.0)
        binding.map.controller.setCenter(startPoint)

        // Detector de Cliques no Mapa
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (isAddingGeofence && p != null) {
                    confirmCreateGeofence(p)
                    return true
                }
                return false
            }
            override fun longPressHelper(p: GeoPoint?): Boolean { return false }
        })
        binding.map.overlays.add(mapEventsOverlay)

        loadGeofences()
        startLocationUpdates()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnActionGeofence.setOnClickListener {
            showGeofenceOptions()
        }

    }

    // --- GEOFENCING ---

    private fun showGeofenceOptions() {
        val options = arrayOf("Criar Nova Cerca (Toque no Mapa)", "Apagar Todas as Cercas", "Cancelar")

        AlertDialog.Builder(this)
            .setTitle("Gerenciar Cercas Virtuais")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        isAddingGeofence = true
                        Toast.makeText(this, "TOQUE NO MAPA para criar a cerca", Toast.LENGTH_LONG).show()
                    }
                    1 -> deleteAllGeofences()
                    2 -> isAddingGeofence = false
                }
            }
            .show()
    }

    private fun confirmCreateGeofence(p: GeoPoint) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Cerca")
            .setMessage("Criar uma zona segura de 100m aqui?")
            .setPositiveButton("Criar") { _, _ ->
                createGeofence(p)
                isAddingGeofence = false
            }
            .setNegativeButton("Cancelar") { _, _ -> isAddingGeofence = false }
            .show()
    }

    private fun createGeofence(p: GeoPoint) {
        val req = CreateGeofenceRequest("Zona Segura", p.latitude, p.longitude, 100.0)
        RetrofitClient.instance.createGeofence(petId, req).enqueue(object : Callback<CreateGeofenceResponse> {
            override fun onResponse(call: Call<CreateGeofenceResponse>, r: Response<CreateGeofenceResponse>) {
                if (r.isSuccessful) {
                    Toast.makeText(applicationContext, "Cerca criada!", Toast.LENGTH_SHORT).show()
                    loadGeofences()
                } else {
                    Toast.makeText(applicationContext, "Erro ao criar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CreateGeofenceResponse>, t: Throwable) {}
        })
    }

    private fun loadGeofences() {
        RetrofitClient.instance.getGeofences(petId).enqueue(object : Callback<GeofenceResponse> {
            override fun onResponse(call: Call<GeofenceResponse>, response: Response<GeofenceResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    // Limpa antigas da tela e da lista
                    binding.map.overlays.removeAll(geofencePolygons)
                    geofencePolygons.clear()

                    val zones = response.body()!!.zones
                    for (zone in zones) {
                        // --- CORREÇÃO PRINCIPAL AQUI ---
                        // 1. Cria o objeto Polygon
                        val circle = Polygon()

                        // 2. Gera os pontos do círculo e atribui ao polígono
                        circle.points = Polygon.pointsAsCircle(GeoPoint(zone.centerLat, zone.centerLng), zone.radius)

                        // 3. Configura a cor (Preenchimento e Borda)
                        circle.fillPaint.color = 0x220000FF // Azul transparente (ARGB)
                        circle.fillPaint.style = Paint.Style.FILL
                        circle.outlinePaint.color = Color.BLUE
                        circle.outlinePaint.strokeWidth = 2f
                        circle.title = zone.name

                        // 4. Adiciona ao mapa
                        binding.map.overlays.add(0, circle) // Adiciona no índice 0 (fundo)
                        geofencePolygons.add(circle) // Guarda na lista para apagar depois
                    }
                    binding.map.invalidate() // Força redesenho
                }
            }
            override fun onFailure(call: Call<GeofenceResponse>, t: Throwable) {}
        })
    }

    private fun deleteAllGeofences() {
        RetrofitClient.instance.getGeofences(petId).enqueue(object : Callback<GeofenceResponse> {
            override fun onResponse(call: Call<GeofenceResponse>, response: Response<GeofenceResponse>) {
                if (response.isSuccessful) {
                    response.body()?.zones?.forEach { z ->
                        RetrofitClient.instance.deleteGeofence(z.id).enqueue(object : Callback<DeleteGeofenceResponse>{
                            override fun onResponse(call: Call<DeleteGeofenceResponse>, r: Response<DeleteGeofenceResponse>) {}
                            override fun onFailure(call: Call<DeleteGeofenceResponse>, t: Throwable) {}
                        })
                    }
                    // Remove visualmente
                    binding.map.overlays.removeAll(geofencePolygons)
                    geofencePolygons.clear()
                    binding.map.invalidate()
                    Toast.makeText(applicationContext, "Cercas removidas", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GeofenceResponse>, t: Throwable) {}
        })
    }

    // --- DADOS DO PET ---

    private fun loadPetDetails() {
        RetrofitClient.instance.getPetDetails(petId).enqueue(object : Callback<PetDto> {
            override fun onResponse(call: Call<PetDto>, r: Response<PetDto>) {
                if (r.isSuccessful && r.body() != null) updateUI(r.body()!!)
            }
            override fun onFailure(call: Call<PetDto>, t: Throwable) {}
        })
    }

    private fun updateUI(pet: PetDto) {
        binding.tvPetName.text = pet.name

        if (pet.isOnline) {
            binding.tvLastSeen.text = "Status: Online"
            binding.tvLastSeen.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.tvLastSeen.text = "Status: Offline"
            binding.tvLastSeen.setTextColor(getColor(android.R.color.darker_gray))
        }

        binding.tvBattery.text = "${pet.batteryLevel}%"
        if (pet.batteryLevel <= 20) binding.tvBattery.setTextColor(getColor(android.R.color.holo_red_dark))
        else binding.tvBattery.setTextColor(getColor(br.edu.fatecpg.patatagapp.R.color.green_700))

        if (!pet.photoUrl.isNullOrEmpty()) {
            var fullUrl = pet.photoUrl
            if (!fullUrl.startsWith("http")) {
                val baseUrl = "http://192.168.X.X:5000" // AJUSTE SEU IP
                fullUrl = "$baseUrl${pet.photoUrl}"
            }
            try { Glide.with(this).load(fullUrl).circleCrop().into(binding.imgPet) } catch (e: Exception){}
        }

        pet.lastLocation?.let { loc ->
            val newPos = GeoPoint(loc.latitude, loc.longitude)

            // Marcador
            if (petMarker == null) {
                petMarker = Marker(binding.map)
                petMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                petMarker?.title = pet.name
                petMarker?.icon = resources.getDrawable(R.drawable.ic_pet_placeholder, null) // Ícone padrão se der erro
                binding.map.overlays.add(petMarker)
            }
            petMarker?.position = newPos

            if (isFirstUpdate) {
                binding.map.controller.animateTo(newPos)
                isFirstUpdate = false
            }
            binding.map.invalidate()
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            RetrofitClient.instance.getPetDetails(petId).enqueue(object : Callback<PetDto> {
                override fun onResponse(call: Call<PetDto>, response: Response<PetDto>) {
                    if (response.isSuccessful && response.body() != null) updateUI(response.body()!!)
                }
                override fun onFailure(call: Call<PetDto>, t: Throwable) {}
            })
            handler.postDelayed(this, updateInterval)
        }
    }

    private fun startLocationUpdates() { handler.post(updateRunnable) }
    private fun stopLocationUpdates() { handler.removeCallbacks(updateRunnable) }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
        stopLocationUpdates()
    }
}