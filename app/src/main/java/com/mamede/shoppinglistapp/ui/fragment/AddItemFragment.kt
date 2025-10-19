package com.mamede.shoppinglistapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.mamede.shoppinglistapp.data.local.AppDataBase
import com.mamede.shoppinglistapp.data.repository.ShoppingItemRepository
import com.mamede.shoppinglistapp.databinding.FragmentAddItemBinding
import com.mamede.shoppinglistapp.ui.factory.AddItemViewModelFactory
import com.mamede.shoppinglistapp.ui.viewmodel.AddItemViewModel
import kotlinx.coroutines.launch
import kotlin.getValue


/**
 * Fragment p/ add ou edita um [ShoppingItem]
 * Valida por meio do [AddItemViewModel] e lida com a nav de volta para lista principal
 */
class AddItemFragment : Fragment() {

    //viewBinding
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    //args p/ recebe o ID do item a ser editado
    //set nav_graph.xml
    private val args: AddItemFragmentArgs by navArgs()

    //viewmodel instanciado com a factory
    private val viewModel: AddItemViewModel by viewModels {
        AddItemViewModelFactory(
            ShoppingItemRepository(AppDataBase.getInstance(requireContext()).shoppingItemDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * infla o layout do fragmente usando viewbinding
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(
            inflater,
            container,
            false)
        return binding.root
    }

    /**
     * call após a view ser criada,
     * set a UI, observadores e listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //vrf se um id de edit e carrega os dados
        viewModel.loadItem(args.itemId)

        setupViewsBaseOnMode()
        setupSaveButton()
        observeViewModel()
        setupInputListeners()
    }

    /**
     * Set a visbilidade inicial dos campos e preenche os dados na edição
     */
    private fun setupViewsBaseOnMode(){
        if (args.itemId == -1L) {
            //edit mode
            binding.tilItemPrice.visibility = View.VISIBLE //show o campo preço

            //observa o item carregado pelo viewmodel p/ preencher os campos
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.item.collect { item ->
                        item?.let {
                            binding.etItemName.setText(it.name)
                            binding.etItemQuantity.setText(it.quantity)
                            binding.edItemPrice.setText(it.price?.toString() ?: "")
                        }
                    }
                }
            }
        } else {
            //add mode
            binding.tilItemPrice.visibility = View.GONE //esconde o campo de preço
        }
    }

    /**
     * Salva o item e navega de volta para a lista principal
     */
    private fun setupSaveButton(){
        binding.buttonSaveItem.setOnClickListener {
            viewModel.saveItem(binding.etItemName.text.toString(),
                binding.etItemQuantity.text.toString(),
                binding.edItemPrice.text.toString())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AddItemViewModel.Event.ValidationFailure -> {
                            //show the error in the TextInputLayouts
                            binding.tilItemName.error = event.nameError
                            binding.tilItemQuantity.error = event.quantityError
                            binding.tilItemPrice.error = event.priceError
                        }
                        is AddItemViewModel.Event.SaveSuccess -> {
                            //navega para a lista anterior
                            findNavController().popBackStack()
                            Toast.makeText(context, "Item salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    /**
     * set listeners nos EditTexts para limpar os erros quando o utilizador digita.
     */
    private fun setupInputListeners() {
        binding.etItemName.addTextChangedListener { binding.tilItemName.error = null }
        binding.etItemQuantity.addTextChangedListener { binding.tilItemQuantity.error = null }
        binding.edItemPrice.addTextChangedListener { binding.tilItemPrice.error = null }
    }

    /**
     * Chamado quando a view do fragmento é destruída.
     * Limpa a referência de binding para evitar memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}