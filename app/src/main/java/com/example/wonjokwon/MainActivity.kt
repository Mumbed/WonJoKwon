package com.example.wonjokwon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(){
    private var adapter: RvAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")
    private val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.itemsRecyclerView) }


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = RvAdapter(this, emptyList())

        recyclerViewItems.adapter = adapter

        updateList()  // list items on recyclerview
        adapter?.setOnItemClickListener {
            val fragment = ItemView()
            val transaction = supportFragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putString("key", it)
            fragment.arguments = bundle

            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }



        val fab=findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener{
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

            // BottomSheetDialog에 표시할 내용을 설정
            // 여기에 여러 메뉴나 내용을 추가하면 됩니다.
            val btnMenuItem1 = view.findViewById<Button>(R.id.newItemUpdate)
            val btnMenuItem3 = view.findViewById<Button>(R.id.settings)

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            btnMenuItem1.setOnClickListener {
                val intent= Intent(this, NewItemsUpdate::class.java)
                startActivity(intent)

            }
            btnMenuItem3.setOnClickListener {
                val intent= Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

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
    private fun updateList() {
        itemsCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<Item>()
            for (doc in it) {
                items.add(Item(doc))
            }
            adapter?.updateList(items)
        }
    }

}