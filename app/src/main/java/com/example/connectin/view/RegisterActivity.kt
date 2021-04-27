package com.example.connectin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import com.example.connectin.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)

        val signT : TextView = findViewById(R.id.sign_TV)
        signT.setOnClickListener {
            val i = Intent(this,LoginActivity::class.java)
            startActivity(i)
        }
    }

    fun radioClicked(view: View) {
        if(view is RadioButton){
            val isChecked = view.isChecked
            if(isChecked){
                when(view.id){
                    R.id.radioButton2 -> {
                        /*val frag = IndvFragement()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.registerFrame,frag)
                            .commit()*/
                    }
                    R.id.radioButton3 -> {
                        val frag = OrgFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.registerFrame,frag)
                            .commit()
                    }
                }
            }
        }
    }


}