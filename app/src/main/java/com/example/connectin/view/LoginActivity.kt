package com.example.connectin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.signin_layout.*

class LoginActivity : AppCompatActivity(){

    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var auth : FirebaseAuth
    private var firstTimeUser = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)
        auth= FirebaseAuth.getInstance()
    }

    fun onClick(view: View){
        val uname = loginUID_EV.text.toString()
        val password  = loginPassword_EV.text.toString()

        when(view.id){

            R.id.loginB ->{

                if(uname.isNotEmpty() || password.isNotEmpty()) {

//                    if (uname.matches(emailPattern.toRegex()))
//                    {
//                        if (password.length >= 5 && password.length <= 12) {
//                            Toast.makeText(this, "User ID: $uname \n Password: ********",
//                                    Toast.LENGTH_SHORT).show()
//                            // val intent = Intent(this,...)
//                            //startActivity(intent)
//                        } else {
//                            Toast.makeText(this, "Password must be 5-12 char long", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                    else{
//                        Toast.makeText(this, "Enter Valid Email address", Toast.LENGTH_LONG).show()
//                    }
//                }

                }
                else
                {
                    Toast.makeText(this,"Please enter all details!",
                        Toast.LENGTH_LONG).show()
                }

                auth.signInWithEmailAndPassword(uname,password).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Logged In",
                            Toast.LENGTH_LONG).show()

                    }
                    else{
                        Toast.makeText(this,"Invalid Credentials",
                            Toast.LENGTH_LONG).show()

                    }
                }
            }

            R.id.forgotPassword_TV -> {

//                if (uname.isNotEmpty() && uname.matches(emailPattern.toRegex())){
//                    Toast.makeText(this, "password sent to $uname",
//                            Toast.LENGTH_LONG).show()
//            }else{
//                    Toast.makeText(this, "Enter a valid username first",
//                            Toast.LENGTH_LONG).show()
//            }

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

            R.id.register_TV ->{
                val i = Intent(this, RegisterActivity::class.java)
                startActivity(i)
                Toast.makeText(this,"directed to register new user page",
                    Toast.LENGTH_LONG).show()

            }

        }
    }
}
