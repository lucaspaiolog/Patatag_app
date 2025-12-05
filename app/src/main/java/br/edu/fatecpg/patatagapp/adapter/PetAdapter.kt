package br.edu.fatecpg.patatagapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.patatagapp.R
import br.edu.fatecpg.patatagapp.databinding.ListItemPetBinding
import br.edu.fatecpg.patatagapp.model.Pet
import com.bumptech.glide.Glide // Import do Glide para carregar imagens

class PetAdapter(
    private var pets: List<Pet>,
    private val onItemClick: (Pet) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ListItemPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetViewHolder(binding)
    }

    override fun getItemCount() = pets.size

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = pets[position]
        holder.bind(pet)
    }

    fun updatePets(newPets: List<Pet>) {
        pets = newPets
        notifyDataSetChanged()
    }

    inner class PetViewHolder(private val binding: ListItemPetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: Pet) {
            binding.tvPetName.text = pet.name
            binding.tvPetStatus.text = pet.status

            // Lógica de Status (Verde/Cinza)
            if (pet.status.contains("Online", ignoreCase = true)) {
                binding.tvPetStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                binding.tvPetStatus.setBackgroundResource(R.drawable.status_background_green)
            } else {
                binding.tvPetStatus.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                binding.tvPetStatus.background = null
            }

            if (!pet.imageUrl.isNullOrEmpty()) {
                var fullUrl = pet.imageUrl

                // Se a URL for relativa (ex: /static/uploads...), adiciona o IP do servidor
                if (!fullUrl.startsWith("http")) {
                    // ⚠️ IMPORTANTE: Use o MESMO IP que está no seu RetrofitClient.kt
                    val baseUrl = "http://192.168.X.X:5000"
                    fullUrl = "$baseUrl${pet.imageUrl}"
                }

                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_pet_placeholder) // Mostra patinha carregando
                    .error(R.drawable.ic_pet_placeholder)       // Mostra patinha se der erro
                    .circleCrop()                               // Corta em círculo
                    .into(binding.imgPet)
            } else {
                // Se não tiver foto, garante que mostra a patinha
                binding.imgPet.setImageResource(R.drawable.ic_pet_placeholder)
            }

            // Clique
            itemView.setOnClickListener {
                onItemClick(pet)
            }
        }
    }
}