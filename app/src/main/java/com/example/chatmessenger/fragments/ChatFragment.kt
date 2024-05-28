@file:Suppress("DEPRECATION")
package com.example.chatmessenger.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatmessenger.R
import com.example.chatmessenger.Utils
import com.example.chatmessenger.adapter.MessageAdapter
import com.example.chatmessenger.databinding.FragmentChatBinding
import com.example.chatmessenger.modal.Messages
import com.example.chatmessenger.mvvm.ChatAppViewModel
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import de.hdodenhof.circleimageview.CircleImageView


class ChatFragment : Fragment() {

    private var imageUri: Uri? = null
    lateinit var args: ChatFragmentArgs
    lateinit var binding : FragmentChatBinding

    lateinit var viewModel : ChatAppViewModel
    lateinit var adapter : MessageAdapter
    lateinit var toolbar: Toolbar

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)
        val textViewStatus = view.findViewById<TextView>(R.id.chatUserStatus)
        val chatBackBtn = toolbar.findViewById<ImageView>(R.id.chatBackBtn)
        val imageSendBtn = toolbar.findViewById<ImageView>(R.id.imageSend)

        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)


        args = ChatFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        Glide.with(view.getContext()).load(args.users.imageUrl!!).placeholder(R.drawable.person).dontAnimate().into(circleImageView);
        textViewName.setText(args.users.username)
        textViewStatus.setText(args.users.status)


        chatBackBtn.setOnClickListener {

            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)

        }

        binding.imageSend.setOnClickListener {

            val options = arrayOf<CharSequence>("Take photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose u picture :)))")
            builder.setItems(options) { dialog, item ->
                when {

                    options[item] == "Take photo" -> {

                        Toast.makeText(context,"take fragment",Toast.LENGTH_SHORT).show()

                    }
                    options[item] == "Choose from Gallery" -> {

                        pickImageFromGallery()

                    }
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()

        }

        binding.sendButton.setOnClickListener {

            viewModel.sendMessage(Utils.getUidLoggedIn(),args.users.userid!!,args.users.username!!,args.users.imageUrl!!)


        }


        viewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {

            initRecyclerView(it)


        })




    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (pickPictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickPictureIntent, PICK_IMAGE_REQUEST)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK &&data != null && data.data != null) {
           imageUri = data.data
            uploadImage()
        }


    }

    private fun uploadImage() {
        when {
            imageUri == null -> Toast.makeText(context, "Please select image first", Toast.LENGTH_SHORT).show()
            else -> {
                val progressDialog = ProgressDialog(context)
                progressDialog.setTitle("Sending Image")
                progressDialog.setMessage("Please wait, we are uploading your image")
                progressDialog.show()



            }
        }
    }


    private fun initRecyclerView(list: List<Messages>) {


        adapter = MessageAdapter()

        val layoutManager = LinearLayoutManager(context)

        binding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true

        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding.messagesRecyclerView.adapter = adapter



    }


}