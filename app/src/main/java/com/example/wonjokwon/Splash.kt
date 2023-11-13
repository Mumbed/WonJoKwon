package com.example.wonjokwon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Splash : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth



        if(auth.currentUser?.uid==null){
            Handler().postDelayed({

                startActivity(Intent(this, JoinActivity::class.java))

                Toast.makeText(this," 회원가입 및 로그인을 해주세요 ", Toast.LENGTH_SHORT).show()
                finish()
            },3000)

        }
        else{
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))

            },3000)

        }


    }
}