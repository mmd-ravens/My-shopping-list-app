package com.mamede.shoppinglistapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa a tabela "shopping_items" na nossa base de dados.

 * @param id A chave primária única para cada item (gerada automaticamente).
 * @param name O nome do item (ex: "Leite").
 * @param quantity A quantidade desejada (ex: "2 caixas", "1 unidade").
 * @param price O preço do item, que pode ser nulo (o utilizador preenche no mercado).
 * @param isInCart Um booleano (true/false) para indicar se o item já está no carrinho.
 */

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val price: Double? = null,

    @ColumnInfo(name = "is_in_cart")
    val isInCart: Boolean = false
)