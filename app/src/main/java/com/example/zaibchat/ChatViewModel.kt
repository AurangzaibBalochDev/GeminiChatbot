package com.example.zaibchat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.apiKey
    )

    fun sendMessage(question: String) {
        val myQuestion = question.trim()

        viewModelScope.launch {

            try {

                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )

           
                   messageList.add(MessageModel(myQuestion, "user"))
                    messageList.add(MessageModel("Typing....", "model"))

                    val response = chat.sendMessage(myQuestion)
                    messageList.removeLast()
                    messageList.add(MessageModel(response.text.toString(), "model"))

                


            } catch (e: Exception) {
                messageList.removeLast()
                messageList.add(MessageModel("Error : " + e.message.toString(), "model"))
            }


        }
    }
}
