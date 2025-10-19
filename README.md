# Projeto avaliativo de Persistência de Dados (D1PDD)

## Descrição do Projeto

Aplicação Android desenvolvida como projeto de avaliação para a disciplina de Persistência de Dados (PDD) da Pós-Graduação Lato Sensu em Desenvolvimento de Sistemas para Dispositivos Móveis. A aplicação permite ao utilizador gerir listas de compras, adicionando itens, quantidades, preços (opcionalmente), marcando itens no carrinho e visualizando o total.

## Funcionalidades Implementadas

-   **CRUD Completo de Itens:** Adicionar, visualizar, editar e apagar itens da lista.
-   **Marcação no Carrinho:** Checkbox para marcar itens já recolhidos.
-   **Cálculo de Total:** Exibição do custo total dos itens marcados no carrinho.
-   **Persistência Local:** Todos os dados são salvos no dispositivo usando Room.
-   **Interface Reativa:** A lista e o total são atualizados automaticamente.
-   **Validação de Formulário:** Mensagens de erro contextuais na tela de adição/edição.
-   **Estado Vazio:** Mensagem informativa quando a lista está vazia.
-   **Desfazer Exclusão:** Opção de desfazer ao apagar um item.

## Arquitetura e Tecnologias Utilizadas

-   **Linguagem:** 100% Kotlin
-   **Arquitetura:** MVVM (Model-View-ViewModel) + Repository Pattern + Separação de Camadas (Data/Domain)
-   **Estrutura da UI:** Atividade Única (Single Activity) com Fragments
-   **Navegação:** Android Navigation Component
-   **Componentes do Android Jetpack:**
    -   Room (SQLite ORM)
    -   ViewModel
    -   Flow (StateFlow, SharedFlow para reatividade e eventos)
    -   ViewBinding
    -   RecyclerView + ListAdapter + DiffUtil
-   **Coroutines:** Para operações assíncronas.

## Como Executar

1.  Clone este repositório.
2.  Abra o projeto no Android Studio (versão [Sua Versão do AS, ex: Giraffe | 2023.3.1] ou superior).
3.  Execute a aplicação num emulador ou dispositivo Android (API 24+).
