package com.example.wonjokwon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_settings, container, false)



        val logout=view.findViewById<TextView>(R.id.Logout)
        logout.setOnClickListener{
            Firebase.auth.signOut()
            val intent= Intent(requireActivity(), LoginAcivity::class.java)
            Toast.makeText(context," 다시 로그인 해주세요 ", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        val join=view.findViewById<TextView>(R.id.join)
        join.setOnClickListener{
            Firebase.auth.signOut()
            val intent= Intent(requireActivity(), JoinActivity::class.java)
            Toast.makeText(context," 다시 회원가입 해줘", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }






        return view
    }

    companion object {
    }
}