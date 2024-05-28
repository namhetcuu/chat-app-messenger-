package com.example.chatmessenger.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.Utils.Companion.MESSAGE_LEFT
import com.example.chatmessenger.Utils.Companion.MESSAGE_RIGHT
import com.example.chatmessenger.modal.Messages


import androidx.recyclerview.widget.DiffUtil

class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessage = listOf<Messages>()

    private val LEFT = 0
    private val RIGHT = 1
    private val VIEW_TYPE_IMAGE_SENT = 2
    private val VIEW_TYPE_IMAGE_RECEIVED = 3


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == RIGHT) {
            val view = inflater.inflate(R.layout.chatitemright, parent, false)
            MessageHolder(view)
        } else if(viewType == LEFT) {
            val view = inflater.inflate(R.layout.chatitemleft, parent, false)
            MessageHolder(view)
        } else if(viewType == VIEW_TYPE_IMAGE_SENT){
            val view = inflater.inflate(R.layout.chatitemleft_image, parent, false)
            MessageHolder(view)
        }else{
            val view = inflater.inflate(R.layout.chatitemright_image, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {

        val message = listOfMessage[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE

        holder.imageofView.visibility = View.VISIBLE

        holder.messageText.text = message.message
        holder.imageofView.setImageResource(message.imageUrl!!.toInt())
        holder.timeOfSent.text = message.time?.substring(0, 5) ?: ""


    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == Utils.getUidLoggedIn()){
            if(listOfMessage[position].imageUrl != "") VIEW_TYPE_IMAGE_SENT else RIGHT
        } else {
            if(listOfMessage[position].imageUrl != "") VIEW_TYPE_IMAGE_RECEIVED else LEFT
        }

    fun setList(newList: List<Messages>) {

        this.listOfMessage = newList

    }

}

class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView.rootView) {
    val messageText: TextView = itemView.findViewById(R.id.show_message)
    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
    val imageofView: ImageView = itemView.findViewById(R.id.imageviewtext)
}
