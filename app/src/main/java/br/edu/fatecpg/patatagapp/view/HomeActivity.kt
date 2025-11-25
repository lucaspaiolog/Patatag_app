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
import br.edu.fatecpg.patatagapp.utils.SessionManager
import br.edu.fatecpg.patatagapp.view.AlertsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var petAdapter: PetAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Atualiza cabeçalho com nome do usuário
        sessionManager.getUserName()?.let {
            binding.tvLocationLabel.text = "Olá, $it"
        }

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadPetsReal()
        // Seleciona o ícone correto na navegação
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun setupRecyclerView() {
        petAdapter = PetAdapter(emptyList()) { pet ->
            val intent = Intent(this, MapaPetActivity::class.java)
            intent.putExtra("PET_ID", pet.id)
            startActivity(intent)
        }
        binding.rvPets.layoutManager = LinearLayoutManager(this)
        binding.rvPets.adapter = petAdapter
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
                R.id.nav_home -> true // Já estamos aqui
                R.id.nav_history -> {
                    // Como não temos HistoryActivity pronta, vamos mostrar um aviso ou redirecionar para o Mapa
                    Toast.makeText(this, "Acesse o histórico pelo Mapa do Pet", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.nav_alerts -> {
                    startActivity(Intent(this, AlertsActivity::class.java))
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
                    val petsLocal = petsApi.map { dto ->
                        Pet(
                            id = dto.id.toString(),
                            name = dto.name,
                            status = if (dto.isOnline) "Online - Bateria: ${dto.batteryLevel}%" else "Offline",
                            imageUrl = dto.photoUrl
                        )
                    }
                    petAdapter.updatePets(petsLocal)
                }
            }
            override fun onFailure(call: Call<PetsResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}