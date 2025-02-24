package com.example.hw45.model.data

import com.example.hw45.model.models.MessageResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChatService {

    @GET("chat/{chatId}")
    suspend fun getChat(@Path("chatId")chatId: Int): List<MessageResponse>

    @FormUrlEncoded
    @POST("chat/send")
    suspend fun sendMessage(
        @Field("chatId") chatId: Int,
        @Field("message") message: String,
        @Field("senderId") senderId: Int,
        @Field("recieverId") recieverId: Int
    ): MessageResponse

    @FormUrlEncoded
    @PUT("chat/{chatId}/message/{messageId}")
    suspend fun updateMessage(
        @Path("chatId") chatId: Int,
        @Path("messageId") messageId: Int,
        @Field("message") message: String
    ): MessageResponse

    @DELETE("chat/{chatId}/message/{messageId}")
    suspend fun deleteMessage(
        @Path("chatId") chatId: Int,
        @Path("messageId") messageId: Int
    ): MessageResponse
}