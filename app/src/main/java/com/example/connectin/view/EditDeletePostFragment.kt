package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditDeletePostFragment : Fragment() {

    lateinit var newPostContentT : EditText
    lateinit var editPostButton : Button
    lateinit var deletePostButton : Button

    var postKey : String = ""

    lateinit var mauth : FirebaseAuth
    lateinit var editdelpostReference : DatabaseReference
    lateinit var currentUserId : String
    lateinit var databaseUserId : String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle : Bundle? = this.arguments
        postKey = bundle?.getString("postKey").toString()

        editdelpostReference = FirebaseDatabase.getInstance().reference.child("Posts").child(postKey)
        mauth = FirebaseAuth.getInstance()
        currentUserId = mauth.currentUser.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.indv_edit_delete_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(activity,"workin?: $postKey",Toast.LENGTH_LONG).show()

        newPostContentT = view.findViewById(R.id.newpostContent_EV)
        editPostButton = view.findViewById(R.id.editpostButton)
        deletePostButton = view.findViewById(R.id.deletepostButton)

        editdelpostReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.child("content").value.toString()
                databaseUserId = snapshot.child("uid").value.toString()
                newPostContentT.setText(content)

                editPostButton.setOnClickListener {
                    editdelpostReference.child("content").setValue(newPostContentT.text.toString())
                    val i = Intent(activity,NavigationActivity::class.java)
                    startActivity(i)
                    Toast.makeText(activity,"Post updated",Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        deletePostButton.setOnClickListener {
            editdelpostReference.removeValue()
            val i = Intent(activity,NavigationActivity::class.java)
            startActivity(i)
            Toast.makeText(activity,"Post deleted",Toast.LENGTH_SHORT).show()
        }
    }
}