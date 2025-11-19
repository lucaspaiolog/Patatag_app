package br.edu.fatecpg.patatagapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.CreatePetRequest
import br.edu.fatecpg.patatagapp.api.CreatePetResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityAdicionarPetBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdicionarPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarPetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        val petTypes = arrayOf("Cachorro", "Gato", "Outro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, petTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPetType.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvAddPhoto.setOnClickListener {
            // TODO: Adicionar lógica para abrir a galeria ou câmera
            Toast.makeText(this, "Abrir galeria...", Toast.LENGTH_SHORT).show()
        }

        binding.btnSalvarPet.setOnClickListener {
            val petName = binding.etPetName.text.toString()
            // O ID do dispositivo é gerado pelo servidor, então não precisamos enviar
            // val deviceId = binding.etDeviceId.text.toString()
            val petType = binding.spinnerPetType.selectedItem.toString()

            if (petName.isEmpty()) {
                Toast.makeText(this, "Nome é obrigatório", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            salvarPetReal(petName, petType)
        }
    }

    private fun salvarPetReal(name: String, type: String) {
        binding.btnSalvarPet.isEnabled = false
        binding.btnSalvarPet.text = "Salvando..."

        val request = CreatePetRequest(name = name, species = type)

        RetrofitClient.instance.createPet(request).enqueue(object : Callback<CreatePetResponse> {
            override fun onResponse(call: Call<CreatePetResponse>, response: Response<CreatePetResponse>) {
                binding.btnSalvarPet.isEnabled = true
                binding.btnSalvarPet.text = "Salvar Pet"

                if (response.isSuccessful && response.body() != null) {
                    val dados = response.body()!!

                    // Mostrar a API Key para o usuário (IMPORTANTE)
                    AlertDialog.Builder(this@AdicionarPetActivity)
                        .setTitle("Pet Criado com Sucesso!")
                        .setMessage("Configure seu ESP32 com esta API Key:\n\n${dados.apiKey}\n\n(Copie agora, ela não será mostrada novamente)")
                        .setPositiveButton("OK") { _, _ ->
                            finish() // Volta para a Home
                        }
                        .setCancelable(false)
                        .show()

                } else {
                    Toast.makeText(applicationContext, "Erro ao criar pet", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreatePetResponse>, t: Throwable) {
                binding.btnSalvarPet.isEnabled = true
                binding.btnSalvarPet.text = "Salvar Pet"
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}