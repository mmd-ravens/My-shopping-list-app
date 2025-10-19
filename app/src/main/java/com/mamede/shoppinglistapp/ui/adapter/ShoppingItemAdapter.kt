package com.mamede.shoppinglistapp.ui.adapter

import android.graphics.Paint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mamede.shoppinglistapp.databinding.ItemShoppingBinding
import com.mamede.shoppinglistapp.domain.ShoppingItem
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter para o RecycleView que exibe a lista de [ShoppingItem]
 * Usa [ListAdapter] e [DiffUtil] para att eficiente
 *
 * @property onItemCheckedChange função lambda que é chamada quando o estado do CheckBox é alterado
 * Recebe o [ShoppingItem] e o novo estado `isChecked`
 */
class ShoppingItemAdapter(
    private val onItemCheckedChange: (ShoppingItem, Boolean) -> Unit
) : ListAdapter<ShoppingItem, ShoppingItemAdapter.ShoppingItemViewHolder>(ShoppingItemDiffCallback()) {

    private var onItemClickListener: ((ShoppingItem) -> Unit)? = null


    /**
     * ViewHolder para cada item da lista
     * Contém as referencias às Views no layout e
     * a lógica para preencher essa Views com dados de um [ShoppingItem]
     *
     * @property binding O objeto ViewBinding que representa o layout do item
     */
    inner class ShoppingItemViewHolder(
        private val binding: ItemShoppingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                //call a fun de click
                    //click(getItem(adapterPosition)) é legado
                    val position = bindingAdapterPosition
                    // valida a posição
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.let { click -> click(getItem(position))}
                    }
            }
        }
            /**
             * Preenche as view do viewholder com dados [ShoppingItem]
             * Configura tbm o listener ao CheckBox
             *
             * @param item O [ShoppingItem] a ser exibido
             */
            fun bind(item: ShoppingItem) {
                binding.textViewItemName.text = item.name
                binding.textViewItemQuantity.text = item.quantity

                //formata e exibe o preço
                binding.textViewItemPrice.text = item.price?.let { formatCurrency(it) } ?: ""

                //set o checkBox
                //removi o listener temporariamente para evitar chamdas recursivas ao definir o estado
                binding.checkBoxItem.setOnCheckedChangeListener(null)
                binding.checkBoxItem.isChecked = item.isInCart
                //re add o listener
                binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
                    onItemCheckedChange(item, isChecked)
                }

                //aplica ou remove o efeito rasura no nome e quantidade
                updateStringThrougth(item.isInCart)
            }


            /**
             * Aplica ou remove o efeito rasura no nome e quantidade
             *
             * @param isChecked True se o item deve parecer rasurado, false caso contrário.
             */
            private fun updateStringThrougth(isChecked: Boolean) {
                val paintFlags = if (isChecked) {
                    Paint.STRIKE_THRU_TEXT_FLAG //add o rasurado
                } else {
                    0 // remove
                }
                binding.textViewItemName.paintFlags =
                    binding.textViewItemName.paintFlags or paintFlags
                binding.textViewItemQuantity.paintFlags =
                    binding.textViewItemQuantity.paintFlags or paintFlags
            }

            /**
             * Formata um valor Double em uma String de moeda no padrão brasileiro (R$).
             *
             * @param value O valor a ser formatado.
             * @return A String formatada (ex: "R$ 5,50").
             */
            private fun formatCurrency(value: Double): String {
                val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
                return format.format(value)
            }
        }

    fun setOnItemClickListener(listener: (ShoppingItem) -> Unit) {
        onItemClickListener = listener
    }

        /**
         * Chamado pelo RecyclerView quando precisa de criar um novo [ShoppingItemViewHolder]
         * Inflar o layout do item e criar o ViewHolder
         *
         * @param parent O ViewGroup no qual o novo ViewHolder será adicionado
         * @param viewType O tipo de view do novo ViewHolder
         * @return Um nova instancia [ShoppingItemViewHolder]
         */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {
         val binding = ItemShoppingBinding.inflate(
             android.view.LayoutInflater.from(
                    parent.context
             ),
                parent,
                false
         )
            return ShoppingItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
        val item = getItem(position)
            holder.bind(item)
        }
    }


/**
 * Classe [DiffUtil.ItemCallback] para calcular as diferenças entre listas de [ShoppingItem]
 * Permite que o [ListAdapter] att o RecyclerView de forma eficiente
 */
class ShoppingItemDiffCallback : DiffUtil.ItemCallback<ShoppingItem>(){

    /**
     * verificar se dois itens representam o mesmo objeto lógico.
     * Compara os IDs únicos dos itens.
     *
     * @param oldItem O item na lista antiga.
     * @param newItem O item na lista nova.
     * @return True se os itens tiverem o mesmo ID, false caso contrário.
     */
    override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Verificar se os dados visuais de dois itens são os mesmos.
     * Como [ShoppingItem] é uma data class, a comparação `==` verifica todas as propriedades.
     *
     * @param oldItem O item na lista antiga.
     * @param newItem O item na lista nova.
     * @return True se todos os dados dos itens forem iguais ou false caso contrário.
     */
    override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem == newItem
    }
}

