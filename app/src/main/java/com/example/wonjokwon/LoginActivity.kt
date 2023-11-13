package com.example.wonjokwon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginAcivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activity)
        auth = Firebase.auth
        val loginBTN=findViewById<Button>(R.id.loginBTN)
        val email = findViewById<TextView>(R.id.emailArea)
        val password = findViewById<TextView>(R.id.passwordArea)


        loginBTN.setOnClickListener{
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent= Intent(this, MainActivity::class.java)


                        Toast.makeText(this," 로그인 성공 ", Toast.LENGTH_SHORT).show()

                        startActivity(intent)

                    } else {
                        Toast.makeText(this,"비밀번호 혹은 이메일이 올바르지않습니다.", Toast.LENGTH_SHORT).show()

                    }
                }

        }
    }
}