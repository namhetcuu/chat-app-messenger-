package com.example.chatmessenger.mvvm

import android.app.ProgressDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatmessenger.MyApplication
import com.example.chatmessenger.SharedPrefs
import com.example.chatmessenger.Utils
import com.example.chatmessenger.Utils.Companion.context
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.modal.RecentChats
import com.example.chatmessenger.modal.Users
import com.example.chatmessenger.notifications.entity.NotificationData
import com.example.chatmessenger.notifications.entity.PushNotification
import com.example.chatmessenger.notifications.entity.Token
import com.example.chatmessenger.notifications.network.RetrofitInstance
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import java.util.*


class ChatAppViewModel : ViewModel() {

    private var myUrl = ""

    private lateinit var storagePostPicRef: StorageReference
    lateinit var storage: FirebaseStorage

    val message = MutableLiveData<String>()
    val firestore = FirebaseFirestore.getInstance()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()


    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    var token: String? = null
    val chatlistRepo = ChatListRepo()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }


    init {

        getCurrentUser()
        getRecentUsers()
    }

    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()


    }


//send Image Message
 fun uploadImage(
    sender: String, receiver: String, friendname: String, friendimage: String,
    imageUri: Uri
) {


    when {
        imageUri == null -> Toast.makeText(context, "Please select image first", Toast.LENGTH_SHORT).show()
        else -> {

            val massageTxt = "massage"


            storage = FirebaseStorage.getInstance()

            storagePostPicRef = storage.reference.child("Chat Images")


            val storagePath = storagePostPicRef.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
             uploadTask = storagePath.putFile(imageUri!!)


            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation storagePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    myUrl = downloadUrl.toString()

                    val hashMap = hashMapOf<String, Any>(
                        "sender" to sender,
                        "receiver" to receiver,
                        "message" to massageTxt,
                        "time" to Utils.getTime(),
                        "imageUrl" to myUrl
                    )


                    val uniqueId = listOf(sender, receiver).sorted()
                    uniqueId.joinToString(separator = "")


                    val friendnamesplit = friendname.split("\\s".toRegex())[0]
                    val mysharedPrefs = SharedPrefs(context)
                    mysharedPrefs.setValue("friendid", receiver)
                    mysharedPrefs.setValue("chatroomid", uniqueId.toString())
                    mysharedPrefs.setValue("friendname", friendnamesplit)
                    mysharedPrefs.setValue("friendimage", friendimage)


                    firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                        .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->


                            val setHashap = hashMapOf<String, Any>(
                                "friendid" to receiver,
                                "time" to Utils.getTime(),
                                "sender" to Utils.getUidLoggedIn(),
                                "message" to myUrl,
                                "friendsimage" to friendimage,
                                "name" to friendname,
                                "person" to "you"
                            )


                            firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                                .set(setHashap)



                            firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                                .update(
                                    "message",
                                    myUrl,
                                    "time",
                                    Utils.getTime(),
                                    "person",
                                    name.value!!
                                )


                            firestore.collection("Tokens").document(receiver).addSnapshotListener { value, error ->


                                if (value != null && value.exists()) {


                                    val tokenObject = value.toObject(Token::class.java)


                                    token = tokenObject?.token!!


                                    val loggedInUsername =
                                        mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]




                                }

                                Log.e("ViewModel", token.toString())



                                if (taskmessage.isSuccessful){

                                    message.value = ""



                                }


                            }
                        }

                }}

        }
    }
}


    // sendMessage

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val context = MyApplication.instance.applicationContext

            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )


            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")


            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)




            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->


                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )


                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(setHashap)



                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )



                      firestore.collection("Tokens").document(receiver).addSnapshotListener { value, error ->


                          if (value != null && value.exists()) {


                              val tokenObject = value.toObject(Token::class.java)


                              token = tokenObject?.token!!


                              val loggedInUsername =
                                  mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]



                              if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {

                                  PushNotification(
                                      NotificationData(loggedInUsername, message.value!!), token!!
                                  ).also {
                                      sendNotification(it)
                                  }

                              } else {


                                  Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                              }


                          }

                          Log.e("ViewModel", token.toString())



                          if (taskmessage.isSuccessful){

                              message.value = ""



                          }


                      }
                   }





        }


    // getting messages

    fun getMessages(friend: String): LiveData<List<Messages>> {

        return messageRepo.getMessages(friend)
    }


    // get RecentUsers


    fun getRecentUsers(): LiveData<List<RecentChats>> {


        return chatlistRepo.getAllChatList()

    }


    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {

            Log.e("ViewModelError", e.toString())
            // showToast(e.message.toString())
        }
    }


    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext


        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->


                if (value!!.exists() && value != null) {

                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!


                    val mysharedPrefs = SharedPrefs(context)
                    mysharedPrefs.setValue("username", users.username!!)


                }


            }


    }




    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser).addOnCompleteListener {

            if (it.isSuccessful){

                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT ).show()


            }

        }


        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")

        val hashMapUpdate = hashMapOf<String, Any>("friendsimage" to imageUrl.value!!, "name" to name.value!!, "person" to name.value!!)



        // updating the chatlist and recent list message, image etc

        firestore.collection("Conversation${friendid}").document(Utils.getUidLoggedIn()).update(hashMapUpdate)

        firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid!!).update("person", "you")



    }


}