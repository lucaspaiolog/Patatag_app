package br.edu.fatecpg.patatagapp.adapter

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.patatagapp.api.AlertDto

class AlertsAdapter(private val alerts: List<AlertDto>) : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    // Cria o visual de cada item (usando um layout simples de texto do Android)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        // Aqui estamos criando um TextView simples via código para não precisar de outro XML
        // Se quiser bonitinho, pode criar um layout xml separado depois.
        val textView = TextView(parent.context)
        textView.setPadding(40, 40, 40, 40) // Margem interna
        textView.textSize = 16f
        return AlertViewHolder(textView)
    }

    // Preenche os dados em cada item
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alerta = alerts[position]

        // Formata a data simples (corta os milissegundos se tiver)
        val dataFormatada = alerta.createdAt.split("T")[0]
        val horaFormatada = alerta.createdAt.split("T")[1].substring(0, 5)

        holder.textView.text = "${getEmoji(alerta.alertType)} ${alerta.message}\n📅 $dataFormatada às $horaFormatada"

        // Muda a cor se for alerta de cerca (Vermelho) ou bateria (Laranja)
        if (alerta.alertType == "geofence") {
            holder.textView.setTextColor(Color.RED)
        } else if (alerta.alertType == "battery") {
            holder.textView.setTextColor(Color.rgb(255, 140, 0)) // Laranja escuro
        } else {
            holder.textView.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = alerts.size

    // Classe que segura a visualização
    class AlertViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    private fun getEmoji(type: String): String {
        return when(type) {
            "geofence" -> "🚨"
            "battery" -> "🪫"
            "offline" -> "🔌"
            else -> "ℹ️"
        }
    }
}