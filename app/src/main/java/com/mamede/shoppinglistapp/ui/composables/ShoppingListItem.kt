package com.mamede.shoppinglistapp.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamede.shoppinglistapp.domain.ShoppingItem
import java.text.NumberFormat
import java.util.Locale


/**
 * Componente visual que representa uma única linha (um item) na lista de compras.
 * Substitui o antigo layout XML `item_shopping.xml` e o `ViewHolder`.
 *
 * @param item O objeto de dados [ShoppingItem] a ser exibido.
 * @param onCheckedChange Função lambda chamada quando o utilizador clica no Checkbox.
 * @param onItemClick Função lambda chamada quando o utilizador clica no cartão (para editar).
 */
@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onChekedChange: (Boolean) -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onItemClick()}, // torna o cartão inteiro clicavel
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // row equivale ao linearlayout, coloca os elementos lado a lado
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically  //centraliza verticalmente
        ) {
            // o CB não precisa de listeners para evitar loops
            // ele reflete o estado (checked) e avisa quando muda (onCheckedChange)
            Checkbox(
                checked = item.isInCart,
                onCheckedChange = onChekedChange
            )

            // espaçamento entre o checkbox e o nome
            Spacer(modifier = Modifier.width(16.dp))

            // coluna com nome e quantidade
            // equivale ao linearlayout vertical, empilha os elementos
            // .weight(1f) faz ele ocupar tdo o espaço disponível (empurrando o preço para o canto)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    // Lógica visual: Se estiver no carrinho, aplica o risco no texto.
                    // No XML, fazíamos isso manipulando PaintFlags no Adapter.
                    textDecoration = if (item.isInCart) TextDecoration.LineThrough else null
                )
                Text(
                    text = item.quantity,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // preço
            if (item.price != null) {
                Text(
                    text = formatCurrency(item.price),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Formata um valor Double para moeda BR.
 */
fun formatCurrency(price: Double): String {
    val ptBr = Locale.Builder().setLanguage("pt").setRegion("BR").build()
    val format = NumberFormat.getCurrencyInstance(ptBr)
    return format.format(price)
}

/**
 * Preview: Permite visualizar o componente no Android Studio sem rodar o app.
 */@Preview(showBackground = true)
@Composable
fun ShoppingListItemPreview() {
    ShoppingListItem(
        item = ShoppingItem(1, "leite", "2 caixas", 2.5, false),
        onChekedChange = {},
        onItemClick = {}
    )
}

