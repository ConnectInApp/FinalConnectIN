package com.example.connectin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    lateinit var useremail : EditText
    lateinit var userpassword : EditText
    lateinit var userconfirmPassword : EditText

    lateinit var mauth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register0_layout)

        mauth = FirebaseAuth.getInstance()

        useremail = findViewById(R.id.uname_EV)
        userpassword = findViewById(R.id.pass_EV)
        userconfirmPassword = findViewById(R.id.Cpass_EV)


        val signT : TextView = findViewById(R.id.sign_TV)
        signT.setOnClickListener {
            val i = Intent(this,LoginActivity::class.java)
            startActivity(i)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = mauth.currentUser
        if(currentUser != null)
        {
            val i = Intent(this,NavigationActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            finish()
        }
    }

    fun onClickButton(view: View) {
        CreateNewAccount()
    }

    private fun CreateNewAccount() {
        val email = useremail.text.toString()
        val password = userpassword.text.toString()
        val confirmPassword = userconfirmPassword.text.toString()

        if(email.isEmpty()) Toast.makeText(this,"Please enter your email!",Toast.LENGTH_SHORT).show()
        else if(password.isEmpty()) Toast.makeText(this,"Please enter your password!",Toast.LENGTH_SHORT).show()
        else if(confirmPassword.isEmpty()) Toast.makeText(this,"Please confirm your password!",Toast.LENGTH_SHORT).show()
        else if(password.compareTo(confirmPassword) != 0) Toast.makeText(this,"password and confirm password do not match",Toast.LENGTH_SHORT).show()
        else {
            mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val i = Intent(this,SetupActivity::class.java)
                    startActivity(i)
                    Toast.makeText(this, "You are now authenticated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else {
                    val msg = it.exception?.message
                    Toast.makeText(this,"Error: $msg",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}