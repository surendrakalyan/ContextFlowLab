package com.example.contextflowlab

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var rvChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    private lateinit var chatAdapter: ChatAdapter

    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Find views
        rvChat = findViewById(R.id.rvChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        // Setup adapter
        chatAdapter = ChatAdapter(messages)

        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = chatAdapter

        // Send button
        btnSend.setOnClickListener {

            val message = etMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: String) {

        // Display user's message
        chatAdapter.addMessage(
            ChatMessage(
                message = message,
                isUser = true
            )
        )

        // Clear input box
        etMessage.text.clear()

        // Scroll to latest message
        rvChat.scrollToPosition(
            chatAdapter.itemCount - 1
        )

        // Disable button while waiting
        btnSend.isEnabled = false

        lifecycleScope.launch {

            try {

                // Send message to Python backend
                val response = ApiClient.apiService.sendMessage(
                    ChatRequest(message)
                )

                if (response.isSuccessful) {

                    val body = response.body()

                    if (!body?.response.isNullOrEmpty()) {

                        // Display AI response
                        chatAdapter.addMessage(
                            ChatMessage(
                                message = body?.response ?: "",
                                isUser = false
                            )
                        )

                    } else {

                        chatAdapter.addMessage(
                            ChatMessage(
                                message = "AI returned an empty response.",
                                isUser = false
                            )
                        )
                    }

                } else {

                    chatAdapter.addMessage(
                        ChatMessage(
                            message = "Server error: ${response.code()}",
                            isUser = false
                        )
                    )
                }

                // Scroll to latest message
                rvChat.scrollToPosition(
                    chatAdapter.itemCount - 1
                )

            } catch (e: Exception) {

                chatAdapter.addMessage(
                    ChatMessage(
                        message = "Connection error: ${e.message}",
                        isUser = false
                    )
                )

                rvChat.scrollToPosition(
                    chatAdapter.itemCount - 1
                )

            } finally {

                // Enable button again
                btnSend.isEnabled = true
            }
        }
    }
}