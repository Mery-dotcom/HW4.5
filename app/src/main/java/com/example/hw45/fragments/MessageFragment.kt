package com.example.hw45.fragments

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.hw45.R
import com.example.hw45.databinding.FragmentMessageBinding
import com.example.hw45.model.models.MessageResponse
import com.example.hw45.view.adapters.MessageAdapters
import com.example.hw45.view.viewModel.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private val viewModel: MessageViewModel by viewModels()
    private val adapter = MessageAdapters(
        clickListener = { message -> showUpdateDialog(message) },
        longClickListener = { message -> onLongClick(message) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        init()
    }

    private fun setupListeners() {
        binding.button.setOnClickListener {
            val message = binding.editText.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(2101, message, 111.toString(), 222.toString())
                binding.editText.text.clear()
            }
        }
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(lifecycle.currentState){
                viewModel.event.observe(viewLifecycleOwner) { event ->
                    when (event) {
                        is MessageViewModel.UiEvent.ShowError -> showToast(event.message)
                        is MessageViewModel.UiEvent.MessageSent -> showToast(event.message)
                        is MessageViewModel.UiEvent.MessageUpdated -> showToast(event.message)
                        is MessageViewModel.UiEvent.MessageDeleted -> showToast(event.message)
                    }
                }
            }
        }

        binding.ibBack.setOnClickListener {
            findNavController().navigate(R.id.listChatFragment)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun init() {
        binding.recyclerView.adapter = adapter
        viewModel.getChat(2101)
    }

    private fun showUpdateDialog(message: MessageResponse) {
        val editText = EditText(requireContext()).apply { setText(message.message) }
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Update Message")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                    val text = editText.text.toString().trim()
                    if (text.isNotEmpty()) {
                        viewModel.updateMessage(message.chatId!!, message.id!!, text)
                    }
                }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
    }

    private fun onLongClick(message: MessageResponse) {
        AlertDialog.Builder(requireContext())
        .setTitle("Delete Message")
        .setMessage("Are you sure you want to delete this message?")
        .setPositiveButton("Delete") { _, _ ->
            viewModel.deleteMessage(message.chatId!!, message.id!!)
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        .create().show()
    }
}