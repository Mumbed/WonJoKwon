package com.example.wonjokwon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
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
        val status="unselled"
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