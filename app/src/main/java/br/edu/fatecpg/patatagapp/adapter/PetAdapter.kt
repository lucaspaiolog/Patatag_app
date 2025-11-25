package br.edu.fatecpg.patatagapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.patatagapp.R
import br.edu.fatecpg.patatagapp.api.RetrofitClient
import br.edu.fatecpg.patatagapp.databinding.ListItemPetBinding
import br.edu.fatecpg.patatagapp.model.Pet
import com.bumptech.glide.Glide // Import do Glide

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

            // Muda cor e fundo se estiver Online
            if (pet.status.contains("Online", ignoreCase = true)) {
                binding.tvPetStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                binding.tvPetStatus.setBackgroundResource(R.drawable.status_background_green)
            } else {
                binding.tvPetStatus.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                binding.tvPetStatus.background = null
            }

            // --- CARREGAR FOTO DO PET (NOVO!) ---
            if (!pet.imageUrl.isNullOrEmpty()) {
                // Monta a URL completa se ela for relativa
                var fullUrl = pet.imageUrl
                if (!fullUrl.startsWith("http")) {
                    // Troque pelo IP do seu PC igual ao RetrofitClient
                    val baseUrl = "http://192.168.15.17:5000"
                    fullUrl = "$baseUrl${pet.imageUrl}"
                }

                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_pet_placeholder) // Mostra patinha enquanto carrega
                    .error(R.drawable.ic_pet_placeholder) // Mostra patinha se der erro
                    .circleCrop() // Deixa redondinha
                    .into(binding.imgPet)
            } else {
                // Se não tiver foto, volta para a patinha padrão
                binding.imgPet.setImageResource(R.drawable.ic_pet_placeholder)
            }

            itemView.setOnClickListener {
                onItemClick(pet)
            }
        }
    }
}