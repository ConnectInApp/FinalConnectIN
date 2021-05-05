package com.example.connectin.presenter

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.connectin.view.NavigationActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditDeletePostPresenter(val view: android.view.View){

    interface View{
        fun initialiseValues()
    }

    fun updateDeletePost(reference:FirebasePresenter,postKey:String,newPostContentT:EditText,editPostButton:Button,activity:Context){
        reference.postReference.child(postKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.child("content").value.toString()
                newPostContentT.setText(content)

                editPostButton.setOnClickListener {
                    reference.postReference.child(postKey).child("content").setValue(newPostContentT.text.toString())
                    val i = Intent(activity, NavigationActivity::class.java)
                    activity.startActivity(i)
                    Toast.makeText(activity,"Post updated", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}