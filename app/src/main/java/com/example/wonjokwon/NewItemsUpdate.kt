package com.example.wonjokwon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewItemsUpdate : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private var adapter: RvAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")
    private val editPrice by lazy {findViewById<EditText>(R.id.editPrice)}
    private val editItemName by lazy {findViewById<EditText>(R.id.editItemName)}
    private val ItemStory by lazy {findViewById<EditText>(R.id.Updatestory)}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_items_update)


        findViewById<Button>(R.id.buttonAddUpdate)?.setOnClickListener {
            addItem()
        }


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
    private fun addItem() {
        auth = Firebase.auth

        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')

        val uid=userEmail
        val name = editItemName.text.toString()
        val story=ItemStory.text.toString()
        val price = editPrice.text.toString().toInt()
        val status="selled"
        if (name.isEmpty()) {
            Snackbar.make(editItemName, "Input name!", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (story.isEmpty()) {
            Snackbar.make(editItemName, "Input story!", Snackbar.LENGTH_SHORT).show()
            return
        }


        val itemMap = hashMapOf(
            "uid" to uid,
            "name" to name,
            "story" to story,
            "price" to price,
            "status" to status

        )
            itemsCollectionRef.document().set(itemMap)
                .addOnSuccessListener { updateList() }.addOnFailureListener {  }

    }
}