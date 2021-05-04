package com.example.connectin.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.connectin.R
import com.example.connectin.model.Chats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


val SERVER_KEY="AAAABCHTT5I:APA91bEeI12sv73FtaX9oFvOIzVhbZnF-zW-0ci7G6ElG5hvxpy00rmmzJwpU-syEPnoOELCbwbWTwrdA_sYEDVLt7LImxjtEUy7N1mC3v7cCxjxC40Hyjf8tDK4ARLnmp_vTgalBpfx"
val NOTIFICATION_URL="https://fcm.googleapis.com/fcm/send"

class ChatActivity: AppCompatActivity() {
    lateinit var currentUserId : String
    lateinit var userReference: DatabaseReference
    lateinit var chatReference: DatabaseReference
    lateinit var senderId:String
    lateinit var mauth : FirebaseAuth

    lateinit var chattingName:TextView
    lateinit var messageUsersList: RecyclerView
    lateinit var inputMessage:TextView
    lateinit var sendB:Button
    lateinit var Recieverprofilephoto:ImageView
    lateinit var messageKey:String

    //new field added
    var name :String = ""

    var chatlist=ArrayList<Chats>()
    lateinit var adapter: ChatsAdapter

    var postKey=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_screen_layout)

        mauth= FirebaseAuth.getInstance()
        senderId= mauth.currentUser.uid

        chattingName=findViewById(R.id.chatting_name_TV)
        messageUsersList=findViewById(R.id.messages_users)
        inputMessage=findViewById(R.id.input_message_EV)
        sendB=findViewById(R.id.send_messageB)
        Recieverprofilephoto=findViewById(R.id.recieverProfPic)

        messageUsersList.layoutManager=LinearLayoutManager(this)

        currentUserId=intent.getStringExtra("currentUserId")!!

         postKey=intent.getStringExtra("postKey")!!


        userReference=FirebaseDatabase.getInstance().getReference()

        userReference.child("Users").child(postKey).addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var name= snapshot.child("username").getValue().toString()
                    chattingName.text=name
                    Toast.makeText(this@ChatActivity,"$name",Toast.LENGTH_LONG).show()

                    Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(Recieverprofilephoto)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        sendB.setOnClickListener {
            SendMessage()
        }

        FetchMessages()

        adapter=ChatsAdapter(chatlist)
        messageUsersList.adapter=adapter

    }

    private fun FetchMessages() {
        userReference.child("Messages").child(senderId).child(postKey)
            .addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.exists()){
                        var messages=snapshot.getValue(Chats::class.java)
                        chatlist.add(messages!!)
                        adapter.notifyDataSetChanged()

                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun SendMessage() {

        var message=inputMessage.text.toString()

        if(message.isEmpty()){
            Toast.makeText(this,"Enter message to send",Toast.LENGTH_LONG).show()
        }
        else{
            var senderRef="Messages/" + senderId + "/" + postKey

            var receiverRef="Messages/" + postKey + "/" + senderId

            chatReference=userReference.child(senderId).child(postKey).push()

             messageKey=chatReference.key!!

            var calendar = Calendar.getInstance()
            val currentDate = SimpleDateFormat("dd-MM-yyyy")
            val chatDate = currentDate.format(calendar.time)

            calendar = Calendar.getInstance()
            val currentTime = SimpleDateFormat("HH:mm")
            val chatTime = currentTime.format(calendar.time)

            var chatName = postKey + chatDate + chatTime
            val hm = HashMap<String,Any>()
            hm["message"] = message

            hm["date"] = chatDate.toString()
            hm["time"] = chatTime.toString()
            hm["from"] = senderId

            val hmBody = HashMap<String,Any>()
            hmBody.put(senderRef + "/" + messageKey , hm)
            hmBody.put(receiverRef + "/" + messageKey , hm)

            userReference.updateChildren(hmBody).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,"Message sent Successfully",Toast.LENGTH_LONG).show()
                    inputMessage.setText("")
                }
                else{
                    var m=it.exception?.message
                    Toast.makeText(this,"Error $m",Toast.LENGTH_LONG).show()
                    inputMessage.setText("")
                }

            }
            getToken(message)
        }

    }
    fun getToken(message:String){

        userReference.child("Users").child(currentUserId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child("username").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        val databaseref=FirebaseDatabase.getInstance().getReference("Users").child(postKey)
        databaseref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val token=snapshot.child("token").value.toString()
                    val name=snapshot.child("name").value.toString()

                    val to=JSONObject()
                    val data=JSONObject()

                    data.put("hisid",postKey)
                    data.put("photo",Recieverprofilephoto)
                    data.put("message",message)
                    data.put("chatId",messageKey)
                    //data.put("title",chattingName.text.toString())
                    data.put("title",name)

                    to.put("to",token)
                    to.put("data",data)
                    sendNotification(to)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
    private fun sendNotification(to: JSONObject) {

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->

                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {

                Log.d("TAG", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)

    }
}


