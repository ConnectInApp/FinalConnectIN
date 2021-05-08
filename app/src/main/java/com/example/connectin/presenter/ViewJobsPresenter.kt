package com.example.connectin.presenter

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ViewJobsPresenter(val view: android.view.View) {

    interface View{
        fun displayAllJobs()
    }

    fun applyJob(reference:FirebasePresenter, title:String,desc:String,salary:String,username:String,postKey:String,activity:Context) {
        reference.userReference.child(reference.currentUserId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val name = snapshot.child("username").value.toString()
                    val pfp = snapshot.child("profileImage").value.toString()
                    val hm = HashMap<String,Any>()
                    hm["username"] = name
                    hm["email"] = reference.auth.currentUser.email
                    hm["profileImg"] = pfp
                    reference.jobsReference.child("$postKey$title").child("applications").child(reference.currentUserId).updateChildren(hm).addOnCompleteListener {
                        Toast.makeText(activity,"Job Applied from presenter", Toast.LENGTH_SHORT).show()
                        if (it.isSuccessful){
                            val um = HashMap<String,Any>()
                            um["title"] = title
                            um["description"] = desc
                            um["salary"] = salary
                            um["username"] = username
                            reference.userReference.child(reference.currentUserId).child("jobsApplied").child("$postKey$title").updateChildren(um).addOnCompleteListener {
                                if(it.isSuccessful){}
                            }
                        } else Toast.makeText(activity,"Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun getResponse(id: String) : String?{
        val urlS = "https://connectin-77e24-default-rtdb.firebaseio.com/Users/$id.json"
        val url = URL(urlS)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        if(connection.responseCode == 200){
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line = reader.readLine()
            var response = ""
            while(line != null) {
                response += line
                line = reader.readLine()
            }
            return response
        } else {
            Log.d("selfProfile","Error: ${connection.responseCode}, ${connection.responseMessage}")
        }
        return null
    }
}