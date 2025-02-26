package com.example.hw45.model.repositories

import android.util.Log
import com.example.hw45.model.core.Either
import com.example.hw45.model.core.RetrofitClient
import com.example.hw45.model.data.ChatService
import com.example.hw45.model.models.MessageResponse
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatService: ChatService
){

    suspend fun getChat(chatId: Int): Either<Throwable, List<MessageResponse>> =
        try {
            val response = chatService.getChat(chatId)
            Either.Success(response)
        } catch (e: Exception){
            Log.e("ChatRepository", "Error: ${e.message}", e)
            Either.Error(e)
        }

    suspend fun sendMessage(chatId: Int, message: String, senderId: String, receiverId: String):
            Either<Throwable, MessageResponse> =
        try {
            val response = chatService.sendMessage(chatId, message, senderId, receiverId)
            Either.Success(response)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error: ${e.message}", e)
            Either.Error(e)
        }

    suspend fun updateMessage(chatId: Int, messageId: Int, message: String):
            Either<Throwable, MessageResponse> =
        try {
            val response = chatService.updateMessage(chatId, messageId, message)
            Either.Success(response)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error: ${e.message}", e)
            Either.Error(e)
        }

    suspend fun deleteMessage(chatId: Int, messageId: Int): Either<Throwable, Unit> =
        try {
            val response = chatService.deleteMessage(chatId, messageId)
            Either.Success(response)
        } catch (e: Exception) {
            Either.Error(e)
        }
}