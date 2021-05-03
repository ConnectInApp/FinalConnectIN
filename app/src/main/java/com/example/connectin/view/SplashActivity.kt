package com.example.connectin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.connectin.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class SplashActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        firebaseAuth = FirebaseAuth.getInstance()



//        Handler().postDelayed({
//            val i = Intent(this,LoginActivity::class.java)
//            //val i = Intent(this,NavigationActivity::class.java)
//            startActivity(i)
//            finish()
//        },2500)

        Handler().postDelayed({
            if (firebaseAuth!!.currentUser == null) {
                val i = Intent(this, LoginActivity::class.java)
                //val i = Intent(this,NavigationActivity::class.java)
                startActivity(i)
                finish()
            }
            else{
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener {
                        if (it.isSuccessful) {
                            val token = it.result?.token
                            val databaseReference =
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(firebaseAuth!!.currentUser.uid)

                            val map: MutableMap<String, Any> = HashMap()
                            map["token"] = token!!
                            databaseReference.updateChildren(map)
                        }
                    })
                val i = Intent(this, LoginActivity::class.java)
                //val i = Intent(this,NavigationActivity::class.java)
                startActivity(i)
                finish()
            }
        },2500)
    }
}