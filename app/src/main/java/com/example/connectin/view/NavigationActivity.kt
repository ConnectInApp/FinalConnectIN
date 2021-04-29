package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class NavigationActivity : AppCompatActivity() {

    lateinit var mauth: FirebaseAuth
    lateinit var userReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        mauth = FirebaseAuth.getInstance()
        userReference = FirebaseDatabase.getInstance().reference.child("User")

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                    R.id.navigation_profile, R.id.navigation_home,R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @Override
    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = mauth.currentUser
        if(currentUser == null)
        {
            sendToLogin()
        } else {
            //checkUserExists()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menu?.add("SIGN OUT")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.title){
            "SIGN OUT" -> {
                Toast.makeText(this,"Signing Out...",Toast.LENGTH_LONG).show()
                mauth.signOut()
                val i = Intent(this,LoginActivity::class.java)
                startActivity(i)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checkUserExists() {
        val current_user_Id = mauth.currentUser.uid
        userReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.hasChild(current_user_Id))
                {
                    sendToSetup()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendToSetup() {
        val i = Intent(this,SetupActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
        finish()
    }

    fun sendToLogin(){
        val i = Intent(this,LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
        finish()
    }
}