package com.example.wonjokwon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = Firebase.firestore

    private val usersInfoCollectionRef = db.collection("UsersInfo")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_settings, container, false)

        auth = Firebase.auth

        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')
        updateUserInfoList {name, birth ->
            // 이곳에서 name을 사용하거나 처리할 작업을 수행
            val textView = view.findViewById<TextView>(R.id.userID)
            textView.post {
                textView.text = "$name 님"
            }
        }
//        view.findViewById<TextView>(R.id.userID).setText("$name+  님")



        val logout=view.findViewById<TextView>(R.id.Logout)
        logout.setOnClickListener{
            Firebase.auth.signOut()

            val intent= Intent(requireActivity(), JoinActivity::class.java)
            Toast.makeText(context," 다시 로그인 해주세요 ", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        return view
    }
    private fun updateUserInfoList(callback: (String,String) -> Unit) {
        val auth = Firebase.auth
        val userEmail = auth.currentUser?.email?.substringBefore('@') ?: ""

        usersInfoCollectionRef.get().addOnSuccessListener { querySnapshot ->
            var name = ""
            var birth=""

            for (doc in querySnapshot) {
                val uid = doc.getString("uid")

                if (userEmail == uid) {
                    name = doc.getString("name") ?: ""
                    birth=doc.getString("birth")?:""
                    break
                }
            }

            callback(name,birth)

        }
    }
    companion object {
    }

}