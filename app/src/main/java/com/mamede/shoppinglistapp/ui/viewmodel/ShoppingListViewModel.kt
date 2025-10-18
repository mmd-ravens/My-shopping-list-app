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


/**
 * ViewModel para a lista de compras [com.mamede.shoppinglistapp.ui.fragment.ShoppingListFragment]
 * Responsável por fornecer dados à UI e processar eventos de usuário.
 *
 * @property shoppingItemRepository O repositório para acessar os dados dos itens da lista de compras
 */
class ShoppingListViewModel(
    private val shoppingItemRepository: ShoppingItemRepository) : ViewModel() {
    private fun formatCurrency(value: Double): String {
        val locale = Locale("pt", "BR") // Define o local para o Brasil (português)
        return NumberFormat.getCurrencyInstance(locale).format(value)
    }
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
            .map { itemList ->
                itemList.filter { it.isInCart && it.price != null}
                    .sumOf  {it.price!! }
            }.map { total ->
                formatCurrency(total)
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                formatCurrency(0.0)
            )
}