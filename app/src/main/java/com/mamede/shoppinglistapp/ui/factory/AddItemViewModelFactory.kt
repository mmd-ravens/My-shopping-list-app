package com.mamede.shoppinglistapp.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mamede.shoppinglistapp.data.repository.ShoppingItemRepository
import com.mamede.shoppinglistapp.ui.viewmodel.AddItemViewModel

/**
 * Factory p/ criar instâncias de [AddItemViewModel]
 *
 * @property repository a instancia do repositorio
 */
class AddItemViewModelFactory(private val repository: ShoppingItemRepository) : ViewModelProvider.Factory {

    /**
     * Cria uma nova instancia do [AddItemViewModel]
     *
     * @param modelClass a class do [AddItemViewModel]
     * @return a instancia do [AddItemViewModel]
     * @throws IllegalArgumentException se a class do viewmodel não for AddItemViewModel
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddItemViewModel::class.java)) {
            return AddItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}