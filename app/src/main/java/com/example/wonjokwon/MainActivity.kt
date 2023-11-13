package com.example.wonjokwon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        print("sibal");
        print("commit test sangmin branch checout2")



        val logout=findViewById<TextView>(R.id.Logout)
        logout.setOnClickListener{
            Firebase.auth.signOut()
            val intent= Intent(this, LoginAcivity::class.java)
            Toast.makeText(this," 다시 로그인 해주세요 ", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        val join=findViewById<TextView>(R.id.join)
        join.setOnClickListener{
            Firebase.auth.signOut()
            val intent= Intent(this, JoinActivity::class.java)
            Toast.makeText(this," 다시 회원가입 해줘", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

}