package com.example.connectin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.signin_layout.*

class LoginActivity : AppCompatActivity(){

    //val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var auth : FirebaseAuth
    //private var firstTimeUser = true
    lateinit var loginButton : Button
    lateinit var userEmail : EditText
    lateinit var userPassword : EditText
    lateinit var registerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)

        registerText = findViewById(R.id.register_TV)
        userEmail = findViewById(R.id.loginUID_EV)
        userPassword = findViewById(R.id.loginPassword_EV)
        loginButton = findViewById(R.id.loginB)

        auth= FirebaseAuth.getInstance()

        val forgotPass = findViewById<TextView>(R.id.forgotPassword_TV)
        forgotPass.setOnClickListener {
            val builder=AlertDialog.Builder(this)
            val inflater=layoutInflater
            val dialogLayout=inflater.inflate(R.layout.forgotpassword_dialog_layout,null)
            builder.setView(dialogLayout)
            var email=dialogLayout.findViewById<EditText>(R.id.emailfp)

            with(builder){
                setTitle("Enter Email Id")
                setPositiveButton("OK"){dialog,which->
                    var mail=email.text.toString()
                    auth.sendPasswordResetEmail(mail).addOnCompleteListener{task->
                        if(task.isSuccessful){
                            Toast.makeText(this@LoginActivity,"Sent email Successfully",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
                setNegativeButton("CANCEL"){dialog,which->
                    Toast.makeText(this@LoginActivity,"Error",
                        Toast.LENGTH_LONG).show()
                }
            }
            builder.show()
        }


        registerText.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = auth.currentUser
        if(currentUser != null)
        {
            val i = Intent(this,NavigationActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            finish()
        }
    }

    fun onClickButton(view: View) {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please enter both your email and password",Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful) {
                    loginAnimation.visibility= VISIBLE
                    loginAnimation.playAnimation()
                    Handler().postDelayed({
                        Toast.makeText(this,"You are now logged in",Toast.LENGTH_SHORT).show()
                        val i = Intent(this,NavigationActivity::class.java)
                        startActivity(i)
                        loginAnimation.visibility= GONE
                        finish()
                    },2000)

                } else {
                    wrongPasswordAnimation.visibility= VISIBLE
                    wrongPasswordAnimation.playAnimation()
                    Handler().postDelayed({
                        Toast.makeText(this, "Error: ${it.exception?.message}",Toast.LENGTH_LONG).show()
                        wrongPasswordAnimation.visibility= GONE
                    },2000)

                }
            }
        }
    }
}
