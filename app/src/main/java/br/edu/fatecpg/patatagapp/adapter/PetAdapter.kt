package br.edu.fatecpg.patatagapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patatag.app.databinding.ListItemPetBinding
import br.edu.fatecpg.patatagapp.model.Pet

class PetAdapter(
    private var pets: List<Pet>,
    private val onItemClick: (Pet) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    // Cria a view (o layout) para cada item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ListItemPetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetViewHolder(binding)
    }

    // Retorna a contagem total de itens
    override fun getItemCount() = pets.size

    // Conecta os dados (Pet) à view (ViewHolder)
    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = pets[position]
        holder.bind(pet)
    }

    // Atualiza a lista de pets
    fun updatePets(newPets: List<Pet>) {
        pets = newPets
        notifyDataSetChanged() // Recarrega a lista
    }

    // Classe interna que gerencia um item da lista
    inner class PetViewHolder(private val binding: ListItemPetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: Pet) {
            binding.tvPetName.text = pet.name
            binding.tvPetStatus.text = pet.status

            // TODO: Mudar a cor do status com base no texto
            // (ex: "Seguro" -> Verde, "Passeando" -> Amarelo)

            // TODO: Carregar a imagem real do pet (usando Glide, por exemplo)
            // if (pet.imageUrl != null) {
            //     Glide.with(itemView.context).load(pet.imageUrl).into(binding.imgPet)
            // } else {
            //     binding.imgPet.setImageResource(R.drawable.ic_pet_placeholder)
            // }

            // Define a ação de clique no item
            itemView.setOnClickListener {
                onItemClick(pet)
            }
        }
    }
}
