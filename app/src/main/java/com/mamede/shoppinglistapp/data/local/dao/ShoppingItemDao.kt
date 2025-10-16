package com.mamede.shoppinglistapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mamede.shoppinglistapp.data.local.entity.ShoppingItem
import kotlinx.coroutines.flow.Flow

/**
* Data Access Object (DAO) para a entidade ShoppingItem.
 * Esta interface define os métodos para interagir com a tabela "shopping_items" no banco de dados.
 * O Room irá gerar automaticamente a implementação desses métodos.
 */

@Dao
interface ShoppingItemDao {

    /**
     * Insere um item na tabela "shopping_items".
     * Se o item existir, ele será atualizado.
     * @param item O item a ser inserido.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItem)

    /**
     * Atualiza um item na tabela "shopping_items".
     * @param item O item a ser atualizado.
     */
    @Update
    suspend fun update(item: ShoppingItem)

    /**
     * Deleta um item da tabela "shopping_items".
     * @param item O item a ser deletado.
     */
    @Delete
    suspend fun delete(item: ShoppingItem)


    /**
     * Busca um item específico na tabela "shopping_items" pelo seu ID.
     * @param id O ID do item a ser buscado.
     * @return O ShoppingItem, ou null se não for encontrado.
     */
    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getItemById(id: Long): ShoppingItem?

    /**
     * Obtém todos os itens da tabela "shopping_items".
     * Retorna um Flow, tornando a query reativa
     * A UI vai ser avisada sempre que os dados mudarem
     * @return Um Flow com a lista de todos os itens.
     */
    @Query("SELECT * FROM shopping_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<ShoppingItem>>

}