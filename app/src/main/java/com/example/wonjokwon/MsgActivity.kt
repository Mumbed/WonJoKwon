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
    val msgsendtext by lazy{findViewById<EditText>(R.id.msgEditTextMessage)}
    val msgsendButton by lazy { findViewById<Button>(R.id.msgButtonSendMessage) }
    private val db: FirebaseFirestore = Firebase.firestore

    private val msgitemsCollectionRef = db.collection("msg")
    override fun onStart() {
        updateList()
        super.onStart()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg)

        val getData=intent.getStringExtra("Receiver")
        val receiverName=getData?.substringBefore(",")
        val itemName=getData?.substringAfter(",")
        updateList()
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = MsgRvAdapter(this, emptyList())
        recyclerViewItems.adapter = adapter



        msgsendButton.setOnClickListener {
            if (receiverName != null) {
                if (itemName != null) {
                    addItem(receiverName,itemName)
                }
            }
        }



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
                        }
                    }

            }
            adapter?.msgUpdateList(items)
        }
    }

    private fun addItem(receiverName:String,itemName:String) {
        auth = Firebase.auth

        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')

        val senderName=userEmail
        val receiverName=receiverName
        val itemName=itemName
        val msg=msgsendtext.text.toString()

        if (msg.isEmpty()) {
            Snackbar.make(msgsendtext, "Input text!", Snackbar.LENGTH_SHORT).show()
            return
        }


        val itemMap = hashMapOf(
            "name" to senderName,
            "receiverName" to receiverName,
            "itemName" to itemName,
            "msg" to msg
        )
        msgitemsCollectionRef.document().set(itemMap)
            .addOnSuccessListener { updateList() }.addOnFailureListener {  }

    }


}