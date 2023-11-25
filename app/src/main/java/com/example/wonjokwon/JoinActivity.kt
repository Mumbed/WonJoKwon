package com.example.wonjokwon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("UsersInfo")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = Firebase.auth

        val email = findViewById<TextView>(R.id.emailArea)
        val password = findViewById<TextView>(R.id.passwordArea)
        val joinbtn = findViewById<Button>(R.id.joinBTN)
        val birthText=findViewById<TextView>(R.id.userBirth)
        val nameText=findViewById<TextView>(R.id.userName)
        val alreadyJoined=findViewById<Button>(R.id.alreadyJoined)

        val already = findViewById<Button>(R.id.already)


        joinbtn.setOnClickListener{

            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent= Intent(this, LoginAcivity::class.java)
                        addItem(nameText.text.toString(),birthText.text.toString())

                        Toast.makeText(this," 회원가입 성공 ", Toast.LENGTH_SHORT).show()
                        startActivity(intent)

                    } else {
                        Toast.makeText(this," 이메일 형식이 올바르지않습니다. ", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        already.setOnClickListener{
            val intent = Intent(this, LoginAcivity::class.java)
            startActivity(intent)
        }
        alreadyJoined.setOnClickListener {

            val intent= Intent(this, LoginAcivity::class.java)
            Toast.makeText(this," 로그인 해주세요 ", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }




    }

    private fun addItem(nameText:String, birthText :String) {
        auth = Firebase.auth

        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')

        val uid=userEmail
        val birth = birthText
        val name=nameText

        val itemMap = hashMapOf(
            "uid" to uid,
            "name" to name,
            "birth" to birth

        )
        itemsCollectionRef.document().set(itemMap)
            .addOnSuccessListener {  }.addOnFailureListener {  }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()  // 뒤로가기 버튼과 동일한 동작을 수행합니다
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 툴바 메뉴 버튼을 설정- menu에 있는 item을 연결하는 부분
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}