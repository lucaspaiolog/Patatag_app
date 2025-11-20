package br.edu.fatecpg.patatagapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.patatagapp.adapter.PetAdapter
import br.edu.fatecpg.patatagapp.api.PetsResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityHomeBinding
import br.edu.fatecpg.patatagapp.model.Pet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var petAdapter: PetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadPetsReal()
    }

    private fun setupRecyclerView() {
        // Inicializa com lista vazia
        petAdapter = PetAdapter(emptyList()) { pet ->
            val intent = Intent(this, MapaPetActivity::class.java)
            intent.putExtra("PET_ID", pet.id)
            startActivity(intent)
        }

        binding.rvPets.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = petAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddPet.setOnClickListener {
            startActivity(Intent(this, AdicionarPetActivity::class.java))
        }

        binding.imgProfile.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    Toast.makeText(this, "Histórico em breve", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_alerts -> {
                    Toast.makeText(this, "Alertas em breve", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadPetsReal() {
        RetrofitClient.instance.getPets().enqueue(object : Callback<PetsResponse> {
            override fun onResponse(call: Call<PetsResponse>, response: Response<PetsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val petsApi = response.body()!!.pets

                    // Converte PetDto (API) -> Pet (Modelo Local)
                    // ATENÇÃO: Usando os nomes em camelCase definidos no ApiModels.kt
                    val petsLocal = petsApi.map { dto ->
                        Pet(
                            id = dto.id.toString(),
                            name = dto.name,
                            // Usando dto.isOnline e dto.batteryLevel (camelCase)
                            status = if (dto.isOnline) "Online - Bateria: ${dto.batteryLevel}%" else "Offline",
                            imageUrl = dto.photoUrl
                        )
                    }

                    // Atualiza o adaptador
                    petAdapter.updatePets(petsLocal)
                } else {
                    Toast.makeText(applicationContext, "Erro ao carregar pets", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PetsResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}