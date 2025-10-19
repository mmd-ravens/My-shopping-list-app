package com.mamede.shoppinglistapp.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mamede.shoppinglistapp.data.repository.ShoppingItemRepository
import com.mamede.shoppinglistapp.ui.viewmodel.ShoppingListViewModel

/**
 * Factory para criar instâncias da ViewModel [ShoppingListViewModel]
 * Necessária para que a ViewModel tem uma dependência no repositório [ShoppingItemRepository] no construtor
 *
 * @property repository A instancia do repositorio a ser injetada no ViewModel
 */
class ShoppingListViewModelFactory(private val repository: ShoppingItemRepository) : ViewModelProvider.Factory {

    /**
     * Cria uma nova instancia do ViewModel
     *
     * @param modelClass A classe do ViewModel a ser criado
     * @return Uma instancia do ViewModel
     * @throws IllegalArgumentException Se a classe do ViewModel não for reconhecida
     */
    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")


    }
}