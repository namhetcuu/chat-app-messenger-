package com.example.chatmessenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.modal.Messages


import com.bumptech.glide.Glide
import com.example.chatmessenger.databinding.ChatitemleftBinding
import com.example.chatmessenger.databinding.ChatitemleftImageBinding
import com.example.chatmessenger.databinding.ChatitemrightBinding
import com.example.chatmessenger.databinding.ChatitemrightImageBinding

class MessageAdapter(var context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfMessage = listOf<Messages>()

    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2
    private val VIEW_TYPE_IMAGE_SENT = 3
    private val VIEW_TYPE_IMAGE_RECEIVED = 4
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_SENT -> SentViewHolder(LayoutInflater.from(context).inflate(R.layout.chatitemright, parent, false)) //right
            ITEM_RECEIVE -> ReceiverViewHolder(inflater.inflate(R.layout.chatitemleft, parent, false))//left

            VIEW_TYPE_IMAGE_SENT -> SentImageViewHolder(inflater.inflate(R.layout.chatitemright_image, parent, false))
            VIEW_TYPE_IMAGE_RECEIVED -> ReceiverImageViewHolder(inflater.inflate(R.layout.chatitemleft_image, parent, false))

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val message = listOfMessage[position]
//
//        holder.messageText.visibility = View.VISIBLE
//        holder.timeOfSent.visibility = View.VISIBLE
//
//
//        holder.imageText.visibility = View.VISIBLE
//        holder.imageText.setImageResource(message.imageUrl!!.toInt())
//        holder.messageText.text = message.message
//
//        holder.timeOfSent.text = message.time?.substring(0, 5) ?: ""
        val message = listOfMessage[position]
        when (holder.itemViewType) {
            ITEM_SENT -> {
                val viewHolder = holder as SentViewHolder
                viewHolder.binding.showMessage.text = message.message
                viewHolder.binding.timeView.text = message.time
            }
            ITEM_RECEIVE -> {
                val viewHolder = holder as ReceiverViewHolder
                viewHolder.binding.showMessage.text = message.message
                viewHolder.binding.timeView.text = message.time
            }
            VIEW_TYPE_IMAGE_SENT -> {
                val viewHolder = holder as SentImageViewHolder
                Glide.with(context!!).load(message.imageUrl).into(viewHolder.binding.imageviewtext)
                viewHolder.binding.timeView.text = message.time
            }
            VIEW_TYPE_IMAGE_RECEIVED -> {
                val viewHolder = holder as ReceiverImageViewHolder
                Glide.with(context!!).load(message.imageUrl).into(viewHolder.binding.imageviewtext)
                viewHolder.binding.timeView.text = message.time
            }
        }


    }

    override fun getItemViewType(position: Int) : Int{
        val message = listOfMessage[position]
        return if (message.sender == Utils.getUidLoggedIn()){
            if(message.imageUrl != null) VIEW_TYPE_IMAGE_SENT else ITEM_SENT
        } else
            if(message.imageUrl != null) VIEW_TYPE_IMAGE_RECEIVED else ITEM_RECEIVE
    }


    fun setList(newList: List<Messages>) {

        this.listOfMessage = newList

    }
    inner class SentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ChatitemrightBinding = ChatitemrightBinding.bind(view)
    }

    inner class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ChatitemleftBinding = ChatitemleftBinding.bind(view)
    }

    inner class SentImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ChatitemrightImageBinding = ChatitemrightImageBinding.bind(view)
    }

    inner class ReceiverImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ChatitemleftImageBinding = ChatitemleftImageBinding.bind(view)
    }

}

//class MessageImageHolder(itemView: View): RecyclerView.ViewHolder(itemView.rootView){
//    val imageText: ImageView = itemView.findViewById(R.id.imageviewtext)
//    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
//}

//class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView.rootView) {
//    val messageText: TextView = itemView.findViewById(R.id.show_message)
//    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
//    val imageText: ImageView = itemView.findViewById(R.id.imageviewtext)
//}

