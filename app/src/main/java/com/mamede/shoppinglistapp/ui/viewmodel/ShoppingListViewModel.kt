package com.mamede.shoppinglistapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamede.shoppinglistapp.data.repository.ShoppingItemRepository
import com.mamede.shoppinglistapp.domain.ShoppingItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * ViewModel para a lista de compras [com.mamede.shoppinglistapp.ui.fragment.ShoppingListFragment]
 * Responsável por fornecer dados à UI e processar eventos de usuário.
 *
 * @property shoppingItemRepository O repositório para acessar os dados dos itens da lista de compras
 */
class ShoppingListViewModel(
    private val shoppingItemRepository: ShoppingItemRepository) : ViewModel() {


    /**
     * Um [StateFlow] que contém a lista de itens da lista de compras [ShoppingItem].
     * A lista vem do repositório e observada continuamente.
     * [stateIn] converte o Flow frio do repositorio num StateFlow quente.
     * Mantendo o último valor emitido em cache
     */
    val items: StateFlow<List<ShoppingItem>> = shoppingItemRepository.getAllItems()
            .stateIn(
                viewModelScope, // scope de coroutine ligado ao ciclo de vida da ViewModel
                SharingStarted.WhileSubscribed(5000L), // mantém o flow ativo por 5s após o ultimo subscriber
                emptyList() //Valor inicial do flow
            )


    /**
     * Um [StateFlow] que mostra o custo total de todos os itens no carrinho.
     * Ele observa o Flow Items e recalcula o total quando a lista muda.
     */
    val totalCostInCart: StateFlow<String> = items
            .map { itemList -> // map trasnforma a lista de itens no valor total
                itemList.filter { it.isInCart && it.price != null} //filtra os itens que estão no carrinho e tem preço
                    .sumOf  {it.price!! } //soma os preços desses itens e o !! pq já filtramos os nulos
            }.map { total -> // transformar o valor Double num texto formatado como moeda
                formatCurrency(total)
            }.stateIn( //converte o resultado num StateFlow
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                formatCurrency(0.0) //valor inicial já formatado
            )

    /**
     * Um [SharedFlow] para enviar EVT únicos para UI
     */
    private val _eventFlow = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _eventFlow.asSharedFlow()

    /**
     * Chamado quando o utilizador clica no CheckBox de um item na lista.
     * Atualiza o estado [isInCart] do item no DB.
     *
     * @param item O [ShoppingItem] que foi selecionado.
     * @param isChecked O novo estado do CheckBox.
     */
    fun onItemCheckedChange(item: ShoppingItem, isChecked: Boolean) {
        viewModelScope.launch {
            // cria uma copia do item com o novo estado e atualiza o DB com update
            shoppingItemRepository.update(item.copy(isInCart = isChecked))
        }

    }

    /**
     * Chama quando desliza para apagar um item
     * Remove o item do DB e emite um evento no SnackBar
     *
     * @param item O [ShoppingItem] a ser apagado
     */
    fun deleteItem(item: ShoppingItem){
        viewModelScope.launch {
            shoppingItemRepository.delete(item)
            //envia evento para UI
            _eventFlow.emit(Event.ShowUndoDeleteItemSnackBar(item))
        }
    }

    /**
     * Restaura um item apagado
     *
     * @param item O [ShoppingItem] a ser restaurado
     */
    fun onUndoDeleteClick(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingItemRepository.insert(item)
        }
    }

    /**
     * Formata um valor Double em uma String de moeda no padrão brasileiro (R$).
     *
     * @param value O valor numérico a ser formatado.
     * @return Uma String representando o valor formatado como moeda, por exemplo, "R$ 123,45".
     */
    private fun formatCurrency(value: Double): String { // formata o valor para o padrão de moeda
        //val locale = Locale("pt", "BR")
        //return NumberFormat.getCurrencyInstance(locale).format(value)
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
        return format.format(value)
    }

    /**
     * Define os eventos que podem ser enviados para a UI
     *
     */
    sealed class Event{
        /**
         * Evento para solicitar a exibição da SnackBar de "Desfazer" após apagar um item
         * @property item O item que foi apagado e pode ser restaurado
         */
        data class ShowUndoDeleteItemSnackBar(val item: ShoppingItem) : Event()
    }
}