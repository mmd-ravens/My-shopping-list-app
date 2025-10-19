package com.mamede.shoppinglistapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamede.shoppinglistapp.data.repository.ShoppingItemRepository
import com.mamede.shoppinglistapp.domain.ShoppingItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


/**
 * ViewModel para o [AddItemFragment]
 * Reponsável por carregar um item existente ou criar um novo
 * valida os dados inseridos e salva o item no DB
 *
 * @property repository O repositorio para acessar os dados dos itens
 */
class AddItemViewModel(private val repository: ShoppingItemRepository) : ViewModel() {

    // Guarda o id do item atual (quando for editar), nulo é quando add
    private var currentItemId: Long? = null

    //Stateflow para colocar os dados do item a ser editado
    private val _item = MutableStateFlow<ShoppingItem?>(null)
    val item: StateFlow<ShoppingItem?> = _item

    //SharedFlow p/ enviar evt ao fragment
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()


    /**
     * Carrega o [ShoppingItem] existente p/ edição
     * Busca o item no repositório e att o stateflow [_item]
     *
     * @param itemId O id do item a ser carregado
     */
    fun loadItem(itemId: Long) {
        //ignora se id não for válido
        if (itemId == -1L) return

        currentItemId = itemId
        viewModelScope.launch {
            val loadedItem = repository.getItemById(itemId)
            _item.value = loadedItem
        }
    }

    /**
     * Salva o item, insere ou att
     * realiza a validação dos campos antes de salvar
     *
     * @param name o nome do item
     * @param quantity a qtt do item inserida
     * @param priceString o preço do item inserido
     */
    fun saveItem(name: String, quantity: String, priceString: String) {
        //valida os campos
        if (name.isBlank()) {
            viewModelScope.launch {
                _events.emit(Event.ValidationFailure(
                    "O nome do item é obrigatório",
                    null,
                    null))
            }
            return
        }
        if (quantity.isBlank()) {
            viewModelScope.launch {
                _events.emit(Event.ValidationFailure(
                    null,
                    "A quantidade do item é obrigatória",
                    null))
            }
            return
        }

        //valida e cnoverte o preço
        val priceDouble = priceString.toDoubleOrNull()
        if (priceString.isNotEmpty() && priceDouble == null) {
            viewModelScope.launch {
                _events.emit(Event.ValidationFailure(
                    null,
                    null,
                    "O preço do item deve ser um número válido"))
            }
            return
        }
        //cria ou att o objeto
        val itemToSave = ShoppingItem(
            id = currentItemId ?: 0, //0 para item novo
            name = name,
            quantity = quantity,
            price = priceDouble,
            //manter o estado isinCart se estiver editando, false se for novo
            isInCart = _item.value?.isInCart ?: false
        )

        //limpa erros e salva
        viewModelScope.launch {
            _events.emit(Event.ValidationFailure(null, null, null))

            try {
                if (currentItemId == null) {
                    repository.insert(itemToSave)
                } else {
                    repository.update(itemToSave)
                }
                _events.emit(Event.SaveSuccess)
            } catch (e: Exception) {
                //assume que qq exceçao é de nome, é para DAO resolver
                _events.emit(Event.ValidationFailure(
                    "Um item com este nome já existe",
                    null,
                    null))
            }
        }
    }


    /**
     * Eventos que podem ser emitidos pelo ViewModel

     */
    sealed class Event {

        /**
         * EVT emitido quando a validação dos campos falha
         *
         * @property nameError Mensagem de erro para o nome do item
         * @property quantityError Mensagem de erro para a quantidade do item
         * @property priceError Mensagem de erro para o preço do item
         */
        data class ValidationFailure(
            val nameError: String?,
            val quantityError: String?,
            val priceError: String?
        ): Event()

        /**
         * EVT emitido qundo o item é salvo com sucesso
         */
        object SaveSuccess: Event()
    }
}