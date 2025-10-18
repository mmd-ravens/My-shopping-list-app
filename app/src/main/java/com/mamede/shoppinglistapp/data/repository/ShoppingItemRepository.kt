package com.mamede.shoppinglistapp.data.repository

import com.mamede.shoppinglistapp.data.local.dao.ShoppingItemDao
import com.mamede.shoppinglistapp.domain.ShoppingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * Repositorio para gerir o acesso aos dados dos itens
 * É o intermediário entre o DAO e o ViewModel, entre a camada de dados e de lógica
 * Não contém nenhuma lógica de negócios
 * Abstrai a origem dos dados e realiza mapeamento entre Entitys e objetos de Domínio
 *
 *
 * @property shoppingItemDao é o DAO para acessar os dados dos itens
 */
class ShoppingItemRepository(private val shoppingItemDao: ShoppingItemDao) {

    /**
     * Obter um flow contendo uma lista de todos os itens da lista de compras
     * Estes já mapeados para um objeto de domínio
     *
     * @return um flow contendo uma lista de todos os itens da lista de compras
     */
    fun getAllItems(): Flow<List<ShoppingItem>>{
        //Busca o flow da entitdade do DAO
        return shoppingItemDao.getAllItems().map { entityList-> //usando o map do flow para transforma cada lista
            entityList.map {  entity ->
                entity.toDomain() } // p/ cada entity na lista, chamamos a fun toDomain()
        }
    }

    /**
     * Busca um item de compra
     *
     * @param id O id do item a ser buscado
     * @return O item de compra correspondente ao id
     */
    suspend fun getItemById(id: Long): ShoppingItem? {
        //busca a entidade do DAO
        val entity = shoppingItemDao.getItemById(id)
        //retorna o objeto de domínio
        return entity?.toDomain()
    }

    /**
     * Insere um item na lista de compras
     * Objeto de domínio é mapeado para entidade antes de ser inserido
     * de [ShoppingItem] para [ShoppingItemEntity]
     *
     * @param item O item a ser inserido
     */
    suspend fun insert(item: ShoppingItem) {
        //converte o objeto de domínio para entidade
        shoppingItemDao.insert(item.toEntity())
    }

    /**
     * Apaga um item da lista de compras
     * Objeto de domínio é mapeado para entidade antes de ser apagado
     * de [ShoppingItem] para [ShoppingItemEntity]
     *
     * @param item O [ShoppingItem] a ser apagado
     */
    suspend fun delete(item: ShoppingItem) {
        shoppingItemDao.delete(item.toEntity())
    }

    /**
     * Atualiza um item na lista de compras
     * Objeto de domínio é mapeado para entidade antes de ser apagado
     *
     * @param item O [ShoppingItem] a ser atualizado
     */
    suspend fun update(item: ShoppingItem){
        shoppingItemDao.update(item.toEntity())
    }
}