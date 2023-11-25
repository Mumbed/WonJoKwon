package com.example.wonjokwon

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MypageFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter: RvAdapter? = null
    private val itemsCollectionRef = db.collection("items")
    private val usersInfoCollectionRef = db.collection("UsersInfo")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun updateList() {
        itemsCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<Item>()
            for (doc in it) {
                items.add(Item(doc))
            }
            adapter?.updateList(items)
        }
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


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_mypage, container, false)
        val recyclerViewItems by lazy { view.findViewById<RecyclerView>(R.id.mypageRV) }


        auth = Firebase.auth

        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')

        updateUserInfoList {name, birth ->
            // 이곳에서 name을 사용하거나 처리할 작업을 수행
            val textView = view.findViewById<TextView>(R.id.userID)
            val birthText=view.findViewById<TextView>(R.id.birth)
            textView.post {
                textView.text = "$name 님의 판매글"
            }
            birthText.post{
                birthText.text="생년월일 $birth"
            }
            println("User name: $name")
        }
        recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        adapter = RvAdapter(requireContext(), emptyList())

        recyclerViewItems.adapter = adapter

        updateList()  // list items on recyclerview

        adapter?.setOnItemClickListener {
            val fragment = ItemView()
            val transaction = childFragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putString("key", it)
            fragment.arguments = bundle

            transaction.replace(R.id.fragment_mypage, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }











        return view
    }

    companion object {
    }
}