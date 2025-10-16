package com.mamede.shoppinglistapp.domain

import com.mamede.shoppinglistapp.data.local.entity.ShoppingItemEntity

/**
 * Modelo de dominio clean
 * Objeto que as fragments e viewmodel vão usar
 * Não contém anotações do Room
 */
class ShoppingItem(
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val price: Double? = null,
    val isInCart: Boolean = false
) {
    /**
     * FUnção mapeadora para converter a Entidade em um objeto de dominio
     */
    fun toEntity() : ShoppingItemEntity = ShoppingItemEntity(id, name, quantity, price, isInCart)

}