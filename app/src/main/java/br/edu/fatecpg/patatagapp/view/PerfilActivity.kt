package br.edu.fatecpg.patatagapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.AuthResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityPerfilBinding
import br.edu.fatecpg.patatagapp.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {
        val nome = sessionManager.getUserName()
        val email = sessionManager.getUserEmail()

        binding.tvUserName.text = nome
        binding.tvUserEmail.text = email
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Edição em breve", Toast.LENGTH_SHORT).show()
        }

        binding.btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notificações em breve", Toast.LENGTH_SHORT).show()
        }

        binding.btnDevices.setOnClickListener {
            Toast.makeText(this, "Dispositivos em breve", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            confirmarLogout()
        }
    }

    private fun confirmarLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair da Conta")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sair") { _, _ ->
                fazerLogoutReal()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun fazerLogoutReal() {
        RetrofitClient.instance.logout().enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                sairDoApp()
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                sairDoApp()
            }
        })
    }

    private fun sairDoApp() {
        // Limpa os dados salvos
        sessionManager.clearSession()

        // Volta para o login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}