package com.mamede.shoppinglistapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mamede.shoppinglistapp.data.local.dao.ShoppingItemDao
import com.mamede.shoppinglistapp.data.local.entity.ShoppingItemEntity

/**
 * Classe principal da Base de dados para o Room
 * Esta classe é abstrata e estende RoomDataBase
 * Serve como ponto de entrada para a base de dados
 *@Database A anotação que define as entitys e a versão do schema
 */
@Database(entities = [ShoppingItemEntity::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    /**
     * Metodo abstrato que retorna o DAO para a entidade ShoppingItemEntity
     */
    abstract fun shoppingItemDao(): ShoppingItemDao

    /**
     * O companion object implementa o padrão sigle
     * Previnindo que várias instancias do banco de dados sejam abertas ao mesmo tempo
     */
    companion object {
        /**
         * @Volatile A anotação garante que o valor da variável Instance
         * seja sempre att e visível para todas as threads de execução
         */
        @Volatile
        private var INSTANCE: AppDataBase? = null

        /**
         * Pega a instância do DB
         * Se a instancia não existir, cria uma nova
         * @param context Contexto da aplicação
         * @return A instância da AppDataBase
         */
        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "shopping_list_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }


    }
}