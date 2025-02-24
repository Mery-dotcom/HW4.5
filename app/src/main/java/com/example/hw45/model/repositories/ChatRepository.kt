package com.example.hw45.model.repositories

import com.example.hw45.model.core.Either
import com.example.hw45.model.core.RetrofitClient
import com.example.hw45.model.models.MessageResponse

class ChatRepository {

    private val chatService = RetrofitClient.chatService

    suspend fun getChat(chatId: Int): Either<Throwable, List<MessageResponse>> =
        try {
            val response = RetrofitClient.chatService.getChat(chatId)
            Either.Success(response)
        } catch (e: Exception){
            Either.Error(e)
        }

    suspend fun sendMessage(chatId: Int, message: String, senderId: Int, recieverId: Int):
            Either<Throwable, MessageResponse> =
        try {
            val newMessage = chatService.sendMessage(chatId, message, senderId, recieverId)
            Either.Success(newMessage)
        } catch (e: Exception) {
            Either.Error(e)
        }

    suspend fun updateMessage(chatId: Int, messageId: Int, message: String):
            Either<Throwable, Unit> =
        try {
            chatService.updateMessage(chatId, messageId, message)
            Either.Success(Unit)
        } catch (e: Exception) {
            Either.Error(e)
        }

    suspend fun deleteMessage(chatId: Int, messageId: Int): Either<Throwable, Unit> =
        try {
            chatService.deleteMessage(chatId, messageId)
            Either.Success(Unit)
        } catch (e: Exception) {
            Either.Error(e)
        }
}