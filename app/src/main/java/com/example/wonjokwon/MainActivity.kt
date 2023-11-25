package com.example.wonjokwon

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        updateList()  // list items on recyclerview


        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = RvAdapter(this, emptyList())

        recyclerViewItems.adapter = adapter

        updateList()  // list items on recyclerview

        itemsCollectionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                // 데이터가 변경되었을 때의 로직
                updateList()
            }
        }


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

//필터링 로직
        val filterButton = findViewById<Switch>(R.id.filter)

        filterButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val status = "unselled"
                itemsCollectionRef.whereEqualTo("status", status).get()
                    .addOnSuccessListener { querySnapshot ->
                        val items = mutableListOf<Item>()
                        for (doc in querySnapshot) {
                            items.add(Item(doc))
                        }
                        adapter?.updateList(items)
                    }
                    .addOnFailureListener { exception ->
                        // 실패할 경우 처리
                    }
            } else {
                // 스위치가 비활성화되면 모든 데이터를 가져와서 리사이클러뷰 업데이트
                itemsCollectionRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val items = mutableListOf<Item>()
                        for (doc in querySnapshot) {
                            items.add(Item(doc))
                        }
                        adapter?.updateList(items)
                    }
                    .addOnFailureListener { exception ->
                        // 실패할 경우 처리
                    }
            }
        }



        val fab=findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener{
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

            // BottomSheetDialog에 표시할 내용을 설정
            // 여기에 여러 메뉴나 내용을 추가하면 됩니다.
            val btnMenuItem1 = view.findViewById<Button>(R.id.newItemUpdate)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            btnMenuItem1.setOnClickListener {
                val intent= Intent(this, NewItemsUpdate::class.java)
                startActivity(intent)

            }


        }



        val bottomnav=findViewById<BottomNavigationView>(R.id.bottomMenu)

        bottomnav.setOnNavigationItemSelectedListener(onBottomNavItemselect)


    }

    private val onBottomNavItemselect= BottomNavigationView.OnNavigationItemSelectedListener{

        when (it.itemId) {

            R.id.msg->{

                val intent= Intent( this, MsgActivity::class.java)
                startActivity(intent)


            }
            R.id.mypage->{
                val fragment = MypageFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            R.id.settings->{

                val fragment = SettingsFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()

            }






        }


        true
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updateList()

    }

    override fun onStart() {
        super.onStart()
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