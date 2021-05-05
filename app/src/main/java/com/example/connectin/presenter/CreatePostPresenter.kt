package com.example.connectin.presenter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connectin.view.NavigationActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CreatePostPresenter(val view:View) {

    var countPost : Long = 0
    fun createPost(reference:FirebasePresenter,postDate:String,postTime:String,postName:String,postContent:EditText,activity:Context){
        //assinging post's order
        reference.postReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    countPost = snapshot.childrenCount
                } else {
                    countPost = 0
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })

        reference.userReference.child(reference.currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val name = snapshot.child("username").value.toString()
                    val profileImg = snapshot.child("profileImage").value.toString()
                    val postMap = HashMap<String,Any>()
                    postMap["uid"] = reference.currentUserId
                    postMap["date"] = postDate
                    postMap["time"] = postTime
                    postMap["content"] = postContent.text.toString()
                    postMap["username"] = name
                    postMap["profileImg"] = profileImg
                    postMap["counter"] = countPost

                    reference.postReference.child(postName).updateChildren(postMap).addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            Toast.makeText(activity,"Post created", Toast.LENGTH_SHORT).show()
                            val i = Intent(activity, NavigationActivity::class.java)
                            activity.startActivity(i)
                        } else {
                            Toast.makeText(activity,"Error while creating post", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}