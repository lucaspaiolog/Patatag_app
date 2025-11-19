package br.edu.fatecpg.patatagapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.AuthResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityPerfilBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {
        // TODO: Precisamos qu o backend tenha um endpoint GET /api/me ou GET /api/profile
        // Como não tem, por enquanto deixei fixo
        // Depois que implementar:
        // @app.route('/api/me') -> return jsonify(current_user.to_dict())

        binding.tvUserName.text = "Usuário Logado" // Substituir por dados reais depois
        binding.tvUserEmail.text = "usuario@email.com"
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
                // Independente se o servidor respondeu OK ou erro, no app nós saímos.
                irParaLogin()
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro ao avisar servidor, saindo localmente...", Toast.LENGTH_SHORT).show()
                irParaLogin()
            }
        })
    }

    private fun irParaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}