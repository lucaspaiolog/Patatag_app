package br.edu.fatecpg.patatagapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.patatagapp.api.LocationDto
import br.edu.fatecpg.patatagapp.databinding.ListItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private val locations: List<LocationDto>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loc = locations[position]

        // Formatar Data
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            val date = inputFormat.parse(loc.timestamp ?: "")
            holder.binding.tvDate.text = if (date != null) outputFormat.format(date) else loc.timestamp
        } catch (e: Exception) {
            holder.binding.tvDate.text = loc.timestamp
        }

        holder.binding.tvCoordinates.text = "${loc.latitude}, ${loc.longitude}"
        holder.binding.tvSpeed.text = "${String.format("%.1f", loc.speed ?: 0.0)} km/h"
    }

    override fun getItemCount() = locations.size
}