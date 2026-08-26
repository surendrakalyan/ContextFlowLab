package com.example.contextflowlab

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {

        val chatMessage = messages[position]

        holder.tvMessage.text = chatMessage.message

        if (chatMessage.isUser) {

            holder.tvMessage.setBackgroundColor(
                Color.rgb(126, 87, 194)
            )

            holder.tvMessage.setTextColor(Color.WHITE)

            holder.tvMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

        } else {

            holder.tvMessage.setBackgroundColor(
                Color.rgb(41, 41, 48)
            )

            holder.tvMessage.setTextColor(Color.WHITE)

            holder.tvMessage.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}