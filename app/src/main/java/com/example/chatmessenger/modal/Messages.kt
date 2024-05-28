package com.example.chatmessenger.modal

data class Messages(
    val sender : String = "",
    val receiver: String = "",
    val message: String = "",
    val time: String? = "",
    val imageUrl: String? = ""

) {

    val id : String get() = "$sender-$receiver-$message-$time-$imageUrl"
}