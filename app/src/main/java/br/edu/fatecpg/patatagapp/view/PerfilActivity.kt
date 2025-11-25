package br.edu.fatecpg.patatagapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.AuthResponse
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.api.UpdateUserRequest
import br.edu.fatecpg.patatagapp.databinding.ActivityPerfilBinding
import br.edu.fatecpg.patatagapp.utils.SessionManager
import br.edu.fatecpg.patatagapp.view.AlertsActivity
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
        setupBottomNav() // Configura a barra inferior
    }

    private fun setupBottomNav() {
        // Marca o item de perfil como selecionado
        binding.bottomNavigation.selectedItemId = R.id.nav_profile

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish() // Fecha o perfil para não empilhar telas
                    true
                }
                R.id.nav_history -> {
                    Toast.makeText(this, "Vá para o mapa para ver o histórico", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.nav_alerts -> {
                    startActivity(Intent(this, AlertsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun loadUserData() {
        binding.tvUserName.text = sessionManager.getUserName()
        binding.tvUserEmail.text = sessionManager.getUserEmail()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // Editar Perfil
        binding.btnEditProfile.setOnClickListener {
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 40, 50, 10)

            val inputNome = EditText(this)
            inputNome.hint = "Novo Nome"
            inputNome.setText(sessionManager.getUserName())
            layout.addView(inputNome)

            val inputSenha = EditText(this)
            inputSenha.hint = "Nova Senha (opcional)"
            layout.addView(inputSenha)

            AlertDialog.Builder(this)
                .setTitle("Editar Perfil")
                .setView(layout)
                .setPositiveButton("Salvar") { _, _ ->
                    val novoNome = inputNome.text.toString()
                    val novaSenha = inputSenha.text.toString()

                    // Chama API
                    val req = UpdateUserRequest(name = novoNome, password = if(novaSenha.isNotEmpty()) novaSenha else null)
                    RetrofitClient.instance.updateUser(req).enqueue(object : Callback<AuthResponse> {
                        override fun onResponse(call: Call<AuthResponse>, r: Response<AuthResponse>) {
                            if (r.isSuccessful && r.body()?.user != null) {
                                sessionManager.saveUser(r.body()!!.user!!)
                                loadUserData()
                                Toast.makeText(applicationContext, "Atualizado!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {}
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Tem certeza?")
                .setPositiveButton("Sim") { _, _ ->
                    RetrofitClient.instance.logout().enqueue(object : Callback<AuthResponse> {
                        override fun onResponse(call: Call<AuthResponse>, r: Response<AuthResponse>) {}
                        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {}
                    })
                    sessionManager.clearSession()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Não", null)
                .show()
        }

        // Botões extras (sem função real no momento)
        binding.btnNotifications.setOnClickListener { Toast.makeText(this, "Configurações em breve", Toast.LENGTH_SHORT).show() }
        binding.btnDevices.setOnClickListener { Toast.makeText(this, "Dispositivos em breve", Toast.LENGTH_SHORT).show() }
    }
}