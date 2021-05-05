package com.example.connectin.presenter

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CommentsPresenter(val view: android.view.View) {

    fun commentClick(commentInput:EditText,activity:Context,currentUserId:String,postKey:String,reference:FirebasePresenter){
        reference.userReference.child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val username = snapshot.child("username").value.toString()
                    commentValidation(commentInput,username,activity!!,currentUserId,postKey,reference)
                    commentInput.setText("")
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun commentValidation(commentInput:EditText,username:String,activity:Context,currentUserId:String,postKey:String,reference:FirebasePresenter) {
        val comment = commentInput.text.toString()
        if(comment.isEmpty()) {
            Toast.makeText(activity,"Empty comment", Toast.LENGTH_SHORT).show()
        } else {
            var calendar = Calendar.getInstance()
            val currentDate = SimpleDateFormat("dd-MM-yyyy")
            val commentDate = currentDate.format(calendar.time)

            calendar = Calendar.getInstance()
            val currentTime = SimpleDateFormat("HH:mm")
            val commentTime = currentTime.format(calendar.time)

            val commentName = currentUserId + commentDate + commentTime
            val hm = HashMap<String,Any>()
            hm["uid"] = currentUserId
            hm["comment"] = comment
            hm["date"] = commentDate.toString()
            hm["time"] = commentTime.toString()
            hm["username"] = username

            reference.postReference.child(postKey).child("Comments").child(commentName).updateChildren(hm).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Toast.makeText(activity,"Comment added!:from presenter again!!", Toast.LENGTH_SHORT).show()
                    commentInput.setText("")
                } else {
                    Toast.makeText(activity,"Error occured!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}