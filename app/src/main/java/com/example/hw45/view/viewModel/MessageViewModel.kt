package com.example.hw45.view.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw45.model.core.Either
import com.example.hw45.model.models.MessageResponse
import com.example.hw45.model.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: ChatRepository
): ViewModel() {

    private val _messages = MutableLiveData<List<MessageResponse>>()
    val messages: LiveData<List<MessageResponse>> get() = _messages

    private val _event = MutableLiveData<UiEvent>()
    val event: LiveData<UiEvent> get() = _event

    sealed class UiEvent {
        data class ShowError(val message: String) : UiEvent()
        data class MessageSent(val message: String) : UiEvent()
        data class MessageUpdated(val message: String) : UiEvent()
        data class MessageDeleted(val message: String) : UiEvent()
    }

    fun getChat(chatId: Int) {
        viewModelScope.launch {
            when (val result = repository.getChat(chatId)) {
                is Either.Success -> _messages.postValue(result.success)
                is Either.Error -> sendEvent(UiEvent.ShowError("Unknown error: ${result.error}"))
                }
            }
        }

    fun sendMessage(chatId: Int, message: String, senderId: String, recieverId: String) {
            viewModelScope.launch {
                when (val result = repository.sendMessage(chatId, message, senderId, recieverId)) {
                    is Either.Success -> {
                        getChat(chatId)
                        sendEvent(UiEvent.MessageSent("Message sent: $message"))
                    }
                    is Either.Error -> sendEvent(UiEvent.ShowError("Failed to send message: ${result.error.message}"))
                }
            }
        }

        fun updateMessage(chatId: Int, messageId: Int, message: String) {
            viewModelScope.launch {
                when (val result = repository.updateMessage(chatId, messageId, message)) {
                    is Either.Success -> {
                        refreshChat(chatId)
                        sendEvent(UiEvent.MessageUpdated("Message updated: $message"))
                    }
                    is Either.Error -> {
                        sendEvent(UiEvent.ShowError("Failed to update message: ${result.error.message}"))
                    }
                }
            }
        }

        fun deleteMessage(chatId: Int, messageId: Int) {
            viewModelScope.launch {
                when (val result = repository.deleteMessage(chatId, messageId)) {
                    is Either.Success -> {
                        refreshChat(chatId)
                        sendEvent(UiEvent.MessageDeleted("Message deleted"))
                    }
                    is Either.Error -> {
                        sendEvent(UiEvent.ShowError("Failed to delete message: ${result.error.message}"))
                    }
                }
            }
        }

    private suspend fun sendEvent(event: UiEvent) {
        _event.postValue(event)
    }

        @SuppressLint("SuspiciousIndentation")
        fun refreshChat(chatId: Int) {
            viewModelScope.launch {
               when (val result = repository.getChat(chatId)) {
                   is Either.Success -> _messages.postValue(result.success)
                   is Either.Error -> {
                       sendEvent(UiEvent.ShowError("Unknown error: ${result.error}"))
                   }
               }
            }
        }
    }