package com.example.wonjokwon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Splash : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = Firebase.firestore

    private val usersInfoCollectionRef = db.collection("UsersInfo")
    private fun updateUserInfoList(callback: (String) -> Unit) {
        val auth = Firebase.auth
        val userEmail = auth.currentUser?.email?.substringBefore('@') ?: ""

        usersInfoCollectionRef.get().addOnSuccessListener { querySnapshot ->
            var name = ""

            for (doc in querySnapshot) {
                val uid = doc.getString("uid")

                if (userEmail == uid) {
                    name = doc.getString("name") ?: ""
                    break
                }
            }

            callback(name)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth

        if(auth.currentUser?.uid==null){
            Handler().postDelayed({

                startActivity(Intent(this, LoginAcivity::class.java))

                Toast.makeText(this," 회원가입 및 로그인을 해주세요 ", Toast.LENGTH_SHORT).show()
                finish()
            },3000)

        }
        else{
            Handler().postDelayed({
                updateUserInfoList { name ->

                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "안녕하세요!! $name 님 !!", Toast.LENGTH_SHORT).show()
                }

            },3000)

        }


    }
}
