package br.edu.fatecpg.patatagapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.patatagapp.api.PetsResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityHomeBinding
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
        // Recarrega a lista sempre que voltar para esta tela (ex: depois de adicionar um pet)
        loadPetsReal()
    }

    private fun setupRecyclerView() {
        petAdapter = PetAdapter(emptyList()) { pet ->
            val intent = Intent(this, MapaPetActivity::class.java)
            // Convertemos o ID String para Int se necessário, ou ajustamos a classe Pet.
            // Assumindo que Pet.id é String no seu modelo local:
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
        // Mostra algum indicativo de carregamento se quiser (ex: ProgressBar)

        RetrofitClient.instance.getPets().enqueue(object : Callback<PetsResponse> {
            override fun onResponse(call: Call<PetsResponse>, response: Response<PetsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val petsApi = response.body()!!.pets

                    // Converte PetDto (API) -> Pet (App Local)
                    val petsLocal = petsApi.map { dto ->
                        Pet(
                            id = dto.id.toString(),
                            name = dto.name,
                            status = if (dto.is_online) "Online - Bateria: ${dto.battery_level}%" else "Offline",
                            imageUrl = dto.photo_url
                        )
                    }

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