package br.edu.fatecpg.patatagapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.AuthResponse
import br.edu.fatecpg.patatagapp.api.RegisterRequest
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityCadastroBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCadastro.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            realizarCadastro(name, email, password)
        }
    }

    private fun realizarCadastro(name: String, email: String, pass: String) {
        binding.btnCadastro.isEnabled = false
        binding.btnCadastro.text = "Criando conta..."

        val request = RegisterRequest(name, email, pass)

        RetrofitClient.instance.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                binding.btnCadastro.isEnabled = true
                binding.btnCadastro.text = "Criar Conta"

                if (response.isSuccessful && response.body()?.user != null) {
                    Toast.makeText(applicationContext, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()

                    // Vai direto para a Home, pois o backend já deve ter logado o usuário (depende da implementação do Flask)
                    // Se o Flask não logar automaticamente no registro, redirecione para LoginActivity
                    val intent = Intent(this@CadastroActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val errorMsg = response.body()?.error ?: "Erro ao criar conta"
                    Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                binding.btnCadastro.isEnabled = true
                binding.btnCadastro.text = "Criar Conta"
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}