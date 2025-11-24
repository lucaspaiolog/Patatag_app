package br.edu.fatecpg.patatagapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.LocationDto
import br.edu.fatecpg.patatagapp.api.PetDto
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import com.bumptech.glide.Glide // Import do Glide para imagens
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

        // Recupera o ID do Pet
        val petIdExtra = intent.getIntExtra("PET_ID", -1)
        if (petIdExtra != -1) {
            petId = petIdExtra
        } else {
            val petIdString = intent.getStringExtra("PET_ID")
            if (petIdString != null) {
                petId = petIdString.toInt()
            } else {
                finish()
                return
            }
        }

        // Inicializa o mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupListeners()

        // Carrega dados iniciais (Nome, Foto, Status)
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

        // Começa o loop de atualização
        startLocationUpdates()
    }

    private fun loadPetDetails() {
        RetrofitClient.instance.getPetDetails(petId).enqueue(object : Callback<PetDto> {
            override fun onResponse(call: Call<PetDto>, response: Response<PetDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val pet = response.body()!!

                    // 1. Atualiza Textos
                    binding.tvPetName.text = pet.name
                    binding.tvLastSeen.text = if (pet.isOnline) "Status: Online" else "Status: Offline"

                    // 2. Atualiza Bateria
                    updateBatteryUI(pet.batteryLevel)

                    // 3. Carrega Foto do Pet (NOVO!)
                    if (!pet.photoUrl.isNullOrEmpty()) {
                        // Se a URL for relativa (/static/...), adiciona o domínio base
                        val fullUrl = if (pet.photoUrl.startsWith("http")) pet.photoUrl else "${RetrofitClient.BASE_URL}${pet.photoUrl.trimStart('/')}"

                        Glide.with(this@MapaPetActivity)
                            .load(fullUrl)
                            .placeholder(R.drawable.ic_pet_placeholder) // Imagem enquanto carrega
                            .error(R.drawable.ic_pet_placeholder) // Imagem se der erro
                            .circleCrop() // Deixa redondinha
                            .into(binding.imgPet)
                    }

                    // 4. Se tiver localização salva, já mostra no mapa
                    pet.lastLocation?.let { loc ->
                        updateMapLocation(LatLng(loc.latitude, loc.longitude))
                    }
                }
            }
            override fun onFailure(call: Call<PetDto>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro ao carregar pet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBatteryUI(level: Int) {
        binding.tvBattery.text = "$level%"

        // Muda a cor se a bateria estiver baixa (< 20%)
        if (level <= 20) {
            binding.tvBattery.setTextColor(getColor(android.R.color.holo_red_dark))
        } else {
            binding.tvBattery.setTextColor(getColor(R.color.green_700))
        }
    }

    // --- LOOP DE ATUALIZAÇÃO (TEMPO REAL) ---

    private val updateRunnable = object : Runnable {
        override fun run() {
            fetchPetDataRealtime()
            handler.postDelayed(this, updateInterval)
        }
    }

    private fun startLocationUpdates() {
        handler.post(updateRunnable)
    }

    private fun stopLocationUpdates() {
        handler.removeCallbacks(updateRunnable)
    }

    private fun fetchPetDataRealtime() {
        // No seu backend atualizado, o endpoint /pets/{id} retorna tudo atualizado (Loc + Bateria)
        // Então vamos chamar ele periodicamente para manter tudo fresco.

        RetrofitClient.instance.getPetDetails(petId).enqueue(object : Callback<PetDto> {
            override fun onResponse(call: Call<PetDto>, response: Response<PetDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val pet = response.body()!!

                    // Atualiza bateria
                    updateBatteryUI(pet.batteryLevel)

                    // Atualiza status
                    binding.tvLastSeen.text = if (pet.isOnline) "Status: Online" else "Status: Offline"

                    // Atualiza mapa se tiver localização
                    pet.lastLocation?.let { loc ->
                        updateMapLocation(LatLng(loc.latitude, loc.longitude))
                    }
                }
            }
            override fun onFailure(call: Call<PetDto>, t: Throwable) {
                // Falha silenciosa para não incomodar o usuário a cada 5s
            }
        })
    }

    private fun updateMapLocation(location: LatLng) {
        mMap?.let { map ->
            map.clear() // Limpa marcador anterior
            map.addMarker(MarkerOptions().position(location).title("Localização Atual"))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates() // Para o loop quando sair da tela
    }
}