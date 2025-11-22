package com.mamede.shoppinglistapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamede.shoppinglistapp.ui.viewmodel.ShoppingListViewModel

/**
 * Tela principal que exibe a lista de compras.
 * Substitui o `ShoppingListFragment` e o seu layout XML.
 *
 * @param viewModel O ViewModel que fornece o estado (lista de itens e total).
 * @param onNavigateToAddItem Callback para navegar para a tela de adicionar.
 * @param onNavigateToEditItem Callback para navegar para a edição (recebe o ID).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel,
    onNavigateToAddItem: () -> Unit,
    onNavigateToEditItem: (Long) -> Unit
) {
    // Estado
    // transforma o StateFlow do ViewModel em um State do compose
    //Em outras palavras, sempre que o ViewModel lança uma nova list, esta tela será recomposta automaticamente

    val items by viewModel.items.collectAsState()
    val totalCost by viewModel.totalCostInCart.collectAsState()

    //Scanfoold, um layout base que fornece lugares práticos
    // barra superior, FAB, Bottombar ...
    Scaffold(
        // FAB
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddItem) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        //Barra inferior com o total
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total no carrinho:", fontWeight = FontWeight.Bold)
                    Text(
                        text = totalCost,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { paddingValues ->
        // o conteudo da tela
        Column(
            modifier = Modifier.fillMaxWidth().padding(paddingValues)
        ) {
            //VRF se a lista está vazia
            //substitui a lógica de View.GONE e View.VISIBLE
            if (items.isEmpty()) {
                // mostrar mensagem de lista vazia
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lista vazia \nClique no +", fontSize = 18.sp
                    )
                }
            } else {
                //lazyColumn -> substitui o recycleview
                //renderiza only itens visíveis
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp) // espaço para o FAB não tapar o último item
                ) {
                    //items() -> substitui o [Adapter]
                    // age sobre a lista de 'items' e cria um ShoppingListItem para cada um
                    items(items = items, key = { it.id }) { item ->
                        ShoppingListItem(
                            item = item,
                            onChekedChange = { isChecked ->
                                viewModel.onItemCheckedChange(item, isChecked)
                            },
                            onItemClick = {
                                onNavigateToEditItem(item.id)
                            }
                        )
                    }
                }
            }

        }

    }

}



