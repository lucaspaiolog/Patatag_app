package br.edu.fatecpg.patatagapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.patatagapp.adapter.HistoryAdapter
import br.edu.fatecpg.patatagapp.api.HistoryResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityHistoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private var petId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebe o ID do pet da tela anterior (Mapa ou Home)
        petId = intent.getIntExtra("PET_ID", -1)

        if (petId == -1) {
            Toast.makeText(this, "Erro: Pet não identificado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)

        loadHistory()
    }

    private fun loadHistory() {
        // Chama a API
        RetrofitClient.instance.getPetHistory(petId).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val locations = response.body()!!.locations

                    if (locations.isEmpty()) {
                        Toast.makeText(this@HistoryActivity, "Nenhum histórico encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        // Configura o adaptador com a lista recebida
                        binding.rvHistory.adapter = HistoryAdapter(locations)
                    }
                } else {
                    Toast.makeText(this@HistoryActivity, "Erro ao carregar histórico", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Toast.makeText(this@HistoryActivity, "Falha na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}