package br.edu.fatecpg.patatagapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.patatagapp.api.AuthResponse
import br.edu.fatecpg.patatagapp.api.LoginRequest
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                fazerLoginReal(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    private fun fazerLoginReal(email: String, pass: String) {
        // Desabilita botão para não clicar duas vezes
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Entrando..."

        // Cria o objeto de dados
        val requestData = LoginRequest(email, pass)

        // Chama a API
        RetrofitClient.instance.login(requestData).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Entrar"

                if (response.isSuccessful && response.body()?.user != null) {
                    // Sucesso!
                    Toast.makeText(applicationContext, "Bem-vindo, ${response.body()!!.user!!.name}!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Erro do servidor (ex: senha errada)
                    Toast.makeText(applicationContext, "Login falhou: Verifique seus dados", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Entrar"

                // Erro de conexão (servidor desligado, sem internet, IP errado)
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
                t.printStackTrace() // Olhe o Logcat para ver o erro detalhado
            }
        })
    }
}