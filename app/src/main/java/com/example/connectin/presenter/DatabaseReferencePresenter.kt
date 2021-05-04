package com.example.connectin.presenter

import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DatabaseReferencePresenter(val view: View) {

    val userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    val postReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Posts")
}