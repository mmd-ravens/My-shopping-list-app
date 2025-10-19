package com.mamede.shoppinglistapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mamede.shoppinglistapp.R
import com.mamede.shoppinglistapp.data.local.AppDataBase
import com.mamede.shoppinglistapp.data.repository.ShoppingItemRepository
import com.mamede.shoppinglistapp.databinding.FragmentShoppingListBinding
import com.mamede.shoppinglistapp.domain.ShoppingItem
import com.mamede.shoppinglistapp.ui.adapter.ShoppingItemAdapter
import com.mamede.shoppinglistapp.ui.factory.ShoppingListViewModelFactory
import com.mamede.shoppinglistapp.ui.viewmodel.ShoppingListViewModel
import kotlinx.coroutines.launch


/**
 * Fragment principal que mostra a lista de itens [ShoppingItem]
 * Add novos itens, marca, desmarca itens no carrinho
 * Apaga itens com gesto de deslizar (com opção de undo) e visualizar o total
 */
class ShoppingListFragment : Fragment() {

    //ViewBinding
    private var _binding: FragmentShoppingListBinding? = null
    //propiedade - não nula - para acesso seguro as views do layout,
    private val binding get() = _binding!!

    //adapter para o RecyclerView
    private lateinit var shoppingItemAdapter: ShoppingItemAdapter

    //viewmodel
    private val viewModel: ShoppingListViewModel by viewModels {
        //cria uma instancia do ShoppingListViewModelFactory
        ShoppingListViewModelFactory(
            //injeta o repositorio
            (ShoppingItemRepository(
                AppDataBase.getInstance(
                    requireContext()).shoppingItemDao())))
    }

    /**
     * Infla o layout do fragment
     *
     * @param inflater O LayourInflate para inflar o layout
     * @param container O ViewGroup onde layout vai ser anexado
     * @param savedInstanceState Estado salvo anteriorimente
     * @return A View raiz do layout inflado
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Depois do onCreateView, quando a view do fragmet estiver criada
     * Configura a UI, observadores e listerners
     *
     * @param view retornada por oncreateview
     * @param savedInstanceState Estado salvo anteriorimente
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //chama as fun de set da UI
        setupRecyclerView()
        setupFab()
        observeViewModel()
        setupSwipeToDelete()

    }

    /**
     * Configura o RecyclerView com o adapter e o layout manager
     */
    private fun setupRecyclerView(){
        //cria o adapter, passa a fun lambda que será chamada quando o checkbox for clicado
        shoppingItemAdapter = ShoppingItemAdapter { item, isChecked ->
            viewModel.onItemCheckedChange(item, isChecked)
        }

        shoppingItemAdapter.setOnItemClickListener { item ->
            //cria action de navegação, passa o id do item como argumento
            val action = ShoppingListFragmentDirections.
            actionShoppingListFragmentToAddItemFragment3(itemId = item.id)
            findNavController().navigate(action)
        }

        //configura o RecyclerView
        binding.shoppingListRecyclerView.apply {
            //configura o layout manager (vertical)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shoppingItemAdapter
        }
    }

    /**
     * Configura o listener de clique para o FloatingActionButton (FAB).
     * Navega para o AddItemFragment quando clicado.
     */
    private fun setupFab() {
        binding.fabAddItem.setOnClickListener {
            // Usa o NavController para navegar para o próximo destino,
            // utilizando a action definida no nav_graph.xml.
            findNavController().navigate(R.id.action_shoppingListFragment_to_addItemFragment3)
        }
    }

    /**
     * Configura os observadores para os StateFlows e SharedFlows do ViewModel.
     * Atualiza a UI quando os dados ou eventos mudam.
     */
    private fun observeViewModel() {
        // Observa a lista de itens e o estado vazio de forma segura quanto ao ciclo de vida
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { items ->
                    // Mostra/Esconde a mensagem de estado vazio
                    binding.textViewEmptyState.visibility = if (items.isEmpty()) VISIBLE else GONE
                    binding.shoppingListRecyclerView.visibility =
                        if (items.isEmpty()) GONE else VISIBLE
                    // Atualiza o adapter com a nova lista de itens
                    shoppingItemAdapter.submitList(items.toList())
                    Log.d("ShoppingListFragment", "Lista atualizada com ${items.size} itens.")
                }
            }
        }

        //observa o custo total
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalCostInCart.collect { totalCost ->
                    //att a TextView do total
                    binding.textViewTotal.text = totalCost
                }
            }
        }

        // Observa os eventos (ex: mostrar SnackBar)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ShoppingListViewModel.Event.ShowUndoDeleteItemSnackBar -> {
                            showUndoSnackBar(event.item)
                        }
                    }
                }
            }
        }
    }

    /**
     * Set o [ItemTouchHelper] para lidar com o gesto de deslizar para apagar itens
     */
    private fun setupSwipeToDelete(){
        val itemTouchHelper =
            object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // não implementa drag and drop
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                // VRF se a posição é válida antes de acessar o item
                if (position != RecyclerView.NO_POSITION) {
                    val item = shoppingItemAdapter.currentList[position]
                    viewModel.deleteItem(item)
                }
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.shoppingListRecyclerView)
    }

    /**
     * Mostra uma SnackBar com uma ação de "Desfazer" para restaurar o item apagado.
     *
     * @param item O [ShoppingItem] que foi apagado.
     */
    private fun showUndoSnackBar(item: ShoppingItem) {
        Snackbar.make(requireView(), "Item '${item.name}' apagado", Snackbar.LENGTH_LONG)
            .setAction("DESFAZER") {
                viewModel.onUndoDeleteClick(item)
            }.show()
    }

    /**
     * Limpa o viewbinding quando o fragment é destruido
     * Isso evita vazamentos de memória
     */
    override fun onDestroyView() {
        super.onDestroyView()
        //define o viewbinding como nulo para liberar a memoria
        _binding = null
    }

}