package br.edu.fatecpg.patatagapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.patatagapp.adapter.AlertsAdapter
import br.edu.fatecpg.patatagapp.api.AlertsResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityAlertsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o RecyclerView
        binding.rvAlerts.layoutManager = LinearLayoutManager(this)

        // Busca os dados
        loadAlerts()
    }

    private fun loadAlerts() {
        // Chama a API real
        RetrofitClient.instance.getAlerts().enqueue(object : Callback<AlertsResponse> {

            override fun onResponse(call: Call<AlertsResponse>, response: Response<AlertsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaDeAlertas = response.body()!!.alerts

                    if (listaDeAlertas.isEmpty()) {
                        Toast.makeText(applicationContext, "Nenhum alerta encontrado", Toast.LENGTH_SHORT).show()
                    }

                    // Conecta a lista ao adaptador para exibir na tela
                    binding.rvAlerts.adapter = AlertsAdapter(listaDeAlertas)

                } else {
                    Toast.makeText(applicationContext, "Erro ao carregar alertas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AlertsResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}