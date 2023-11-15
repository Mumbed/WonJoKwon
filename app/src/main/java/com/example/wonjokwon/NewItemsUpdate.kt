package com.example.wonjokwon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewItemsUpdate : AppCompatActivity() {

    private var adapter: RvAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")
    private var snapshotListener: ListenerRegistration? = null

    private val checkAutoID by lazy { findViewById<CheckBox>(R.id.checkAutoID) }
    private val editID by lazy { findViewById<EditText>(R.id.editID) }
    private val editPrice by lazy {findViewById<EditText>(R.id.editPrice)}
    private val editItemName by lazy {findViewById<EditText>(R.id.editItemName)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_items_update)


        checkAutoID.setOnClickListener {
            editID.isEnabled = !checkAutoID.isChecked
            if (!editID.isEnabled)
                editID.setText("")
        }

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
        val name = editItemName.text.toString()
        if (name.isEmpty()) {
            Snackbar.make(editItemName, "Input name!", Snackbar.LENGTH_SHORT).show()
            return
        }
        val price = editPrice.text.toString().toInt()
        val autoID = checkAutoID.isChecked
        val itemID = editID.text.toString()
        if (!autoID and itemID.isEmpty()) {
            Snackbar.make(editID, "Input ID or check Auto-generate ID!", Snackbar.LENGTH_SHORT).show()
            return
        }
        val itemMap = hashMapOf(
            "name" to name,
            "price" to price
        )
        if (autoID) {
            itemsCollectionRef.add(itemMap)
                .addOnSuccessListener { updateList() }.addOnFailureListener {  }
        } else {
            itemsCollectionRef.document(itemID).set(itemMap)
                .addOnSuccessListener { updateList() }.addOnFailureListener {  }
        }
    }
}