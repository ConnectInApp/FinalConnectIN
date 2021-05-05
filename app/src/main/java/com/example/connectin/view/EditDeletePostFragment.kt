package com.example.connectin.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.connectin.R
import com.example.connectin.presenter.EditDeletePostPresenter
import com.example.connectin.presenter.FirebasePresenter
import kotlinx.android.synthetic.main.indv_edit_delete_post.*

class EditDeletePostFragment : Fragment(), EditDeletePostPresenter.View {

    lateinit var newPostContentT : EditText
    lateinit var editPostButton : Button
    lateinit var deletePostButton : Button

    var postKey : String = ""

    lateinit var reference : FirebasePresenter
    lateinit var editDeletePostPresenter: EditDeletePostPresenter
    lateinit var currentUserId : String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle : Bundle? = this.arguments
        postKey = bundle?.getString("postKey").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.indv_edit_delete_post,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing presenter reference
        reference = FirebasePresenter(view)
        editDeletePostPresenter = EditDeletePostPresenter(view)

        initialiseValues()

        editDeletePostPresenter.updateDeletePost(reference,postKey,newPostContentT,editPostButton,requireActivity())

        deletePostButton.setOnClickListener {
            delete_post_animation.visibility= VISIBLE
            delete_post_animation.playAnimation()
            Handler().postDelayed({
                reference.postReference.child(postKey).removeValue()
                val i = Intent(activity,NavigationActivity::class.java)
                startActivity(i)
                Toast.makeText(activity,"Post deleted",Toast.LENGTH_SHORT).show()
                delete_post_animation.visibility= View.INVISIBLE
            },1500)

        }
    }

    override fun initialiseValues() {
        newPostContentT = view?.findViewById(R.id.newpostContent_EV)!!
        editPostButton = view?.findViewById(R.id.editpostButton)!!
        deletePostButton = view?.findViewById(R.id.deletepostButton)!!
    }
}