package com.example.connectin.presenter

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebasePresenter(val view: View) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUserId = auth.currentUser.uid

    val userProfileImgRef : StorageReference = FirebaseStorage.getInstance().getReference().child("profileImgs")

    val userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    val postReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Posts")
    val connectionReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Connections")
    val likesReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Likes")
    val dislikesReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Dislikes")
    val endorsementReference : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Endorsements")
    val jobsReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Jobs")
    val followersReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Follows")
    val connectionReqRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("ConnectionRequests")
    val blockReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Blocks")
}