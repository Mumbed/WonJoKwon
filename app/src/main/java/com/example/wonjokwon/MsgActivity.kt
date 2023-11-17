package com.example.wonjokwon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MsgActivity : AppCompatActivity() {

    private var adapter: MsgRvAdapter? = null
    private lateinit var auth: FirebaseAuth
    val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.msgItemsRecyclerView) }

    private val db: FirebaseFirestore = Firebase.firestore

    private val msgitemsCollectionRef = db.collection("msg")
    override fun onStart() {
        updateList()
        super.onStart()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg)

        updateList()
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = MsgRvAdapter(this, emptyList())
        recyclerViewItems.adapter = adapter
        updateList()

    }

    private fun updateList() {
        auth = Firebase.auth

        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')
        msgitemsCollectionRef.get().addOnSuccessListener {

            val items = mutableListOf<MsgItem>()
            for (doc in it) {
                msgitemsCollectionRef.document(doc.id).get()
                    .addOnSuccessListener {
                        if(userEmail==(it["receiverName"].toString())){
                            items.add(MsgItem(doc))
                            adapter?.msgUpdateList(items)
                        }
                    }

            }
            adapter?.msgUpdateList(items)
        }
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updateList()

    }

    override fun onStop() {
        super.onStop()
        updateList()
    }

}