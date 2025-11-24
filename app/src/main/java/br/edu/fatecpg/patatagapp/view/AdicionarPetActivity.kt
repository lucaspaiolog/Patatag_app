package br.edu.fatecpg.patatagapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.CreatePetRequest
import br.edu.fatecpg.patatagapp.api.CreatePetResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.api.UploadResponse
import br.edu.fatecpg.patatagapp.databinding.ActivityAdicionarPetBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AdicionarPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarPetBinding
    private var photoUrl: String = ""
    private var selectedImageUri: Uri? = null

    // Lançador para abrir a galeria
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.imgPet.setImageURI(selectedImageUri) // Mostra preview
            binding.tvAddPhoto.text = "Foto Selecionada!"
        }
    }

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
        binding.btnBack.setOnClickListener { finish() }

        binding.tvAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        binding.btnSalvarPet.setOnClickListener {
            val petName = binding.etPetName.text.toString()
            val petType = binding.spinnerPetType.selectedItem.toString()

            if (petName.isEmpty()) {
                Toast.makeText(this, "Nome é obrigatório", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSalvarPet.isEnabled = false
            binding.btnSalvarPet.text = "Salvando..."

            if (selectedImageUri != null) {
                uploadImageAndSave(petName, petType)
            } else {
                salvarPetReal(petName, petType, "")
            }
        }
    }

    private fun uploadImageAndSave(name: String, type: String) {
        val file = getFileFromUri(selectedImageUri!!)
        if (file != null) {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            RetrofitClient.instance.uploadImage(body).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        // Upload com sucesso, agora salva o pet com a URL
                        val url = response.body()!!.url
                        salvarPetReal(name, type, url)
                    } else {
                        Toast.makeText(applicationContext, "Erro ao enviar foto", Toast.LENGTH_SHORT).show()
                        salvarPetReal(name, type, "") // Salva sem foto
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Falha upload: ${t.message}", Toast.LENGTH_SHORT).show()
                    salvarPetReal(name, type, "")
                }
            })
        } else {
            salvarPetReal(name, type, "")
        }
    }

    private fun salvarPetReal(name: String, type: String, url: String) {
        val request = CreatePetRequest(name = name, species = type, photoUrl = url)

        RetrofitClient.instance.createPet(request).enqueue(object : Callback<CreatePetResponse> {
            override fun onResponse(call: Call<CreatePetResponse>, response: Response<CreatePetResponse>) {
                binding.btnSalvarPet.isEnabled = true
                binding.btnSalvarPet.text = "Salvar Pet"

                if (response.isSuccessful && response.body() != null) {
                    val dados = response.body()!!

                    AlertDialog.Builder(this@AdicionarPetActivity)
                        .setTitle("Pet Criado!")
                        .setMessage("API Key do ESP32:\n\n${dados.apiKey}")
                        .setPositiveButton("OK") { _, _ -> finish() }
                        .setCancelable(false)
                        .show()
                } else {
                    Toast.makeText(applicationContext, "Erro ao criar pet", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreatePetResponse>, t: Throwable) {
                binding.btnSalvarPet.isEnabled = true
                binding.btnSalvarPet.text = "Salvar Pet"
                Toast.makeText(applicationContext, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Função auxiliar para transformar URI em Arquivo
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}