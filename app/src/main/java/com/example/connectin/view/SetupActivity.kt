package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.connectin.R

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)

    }

    fun radioClicked(view: View) {
        if(view is RadioButton){
            val isChecked = view.isChecked
            if(isChecked){
                when(view.id){
                    R.id.radioButton2 -> {
                        val frag = IndvFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.registerFrame,frag)
                            .commit()
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