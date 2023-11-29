package com.example.wonjokwon

import android.app.AlertDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class MsgActivity : AppCompatActivity() {

    private var adapter: MsgRvAdapter? = null
    private lateinit var auth: FirebaseAuth
    val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.msgItemsRecyclerView) }
    private var receiverName = ""
    private var receiveruid = ""

    private var titlename = ""
    private val db: FirebaseFirestore = Firebase.firestore

    private val msgitemsCollectionRef = db.collection("msg")
    private val usersInfoCollectionRef = db.collection("UsersInfo")

    override fun onStart() {
        updateList()
        super.onStart()
    }

    private fun addItem(receiverName: String, itemName: String, msgsendtext: String) {
        auth = Firebase.auth
        val receiverName = receiverName
        val itemName = itemName
        val msg = msgsendtext
        if (msg.isEmpty()) {
            Toast.makeText(this, " please type text! ", Toast.LENGTH_SHORT).show()

            return
        }
        ueserInfo { name ->


            val itemMap = hashMapOf(
                "reply" to "reply",
                "name" to name,
                "receiverName" to receiverName,
                "itemName" to itemName,
                "msg" to msg
            )
            msgitemsCollectionRef.document().set(itemMap)
                .addOnSuccessListener {
                }.addOnFailureListener { }


        }


    }

    private fun QueryList(itemID: String, callback: () -> Unit) {
        msgitemsCollectionRef.document(itemID).get().addOnSuccessListener { documentSnapshot ->
            receiverName = documentSnapshot["name"].toString()
            titlename = documentSnapshot["itemName"].toString()
            usersInfoCollectionRef.get().addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    usersInfoCollectionRef.document(doc.id).get().addOnSuccessListener { userSnapshot ->
                        if (receiverName == userSnapshot["name"].toString()) {
                            receiveruid = userSnapshot["uid"].toString()
                        }
                    }
                }
                // 데이터 검색이 완료되면 콜백을 호출합니다.
                callback.invoke()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg)

        updateList()
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = MsgRvAdapter(this, emptyList())
        recyclerViewItems.adapter = adapter
        updateList()

        adapter?.setOnItemClickListener {

            val builder2 = AlertDialog.Builder(this)
            val inflater2 = layoutInflater.inflate(R.layout.msgdialog2, null)
            builder2.setView(inflater2)

            val msg = inflater2.findViewById<EditText>(R.id.remsgText)
            val sendBtn = inflater2.findViewById<Button>(R.id.buttonReply)
            QueryList(it){
                inflater2.findViewById<TextView>(R.id.msgDialogTitle2)
                    .setText(receiverName + "님에게 답장 보내기")

                val dialog2 = builder2.create()

                sendBtn.setOnClickListener {
                    addItem(receiveruid, titlename, msg.text.toString())
                    dialog2.dismiss()


                }
                dialog2.dismiss()
                dialog2.show()

            }



        }
        msgitemsCollectionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                // 데이터가 변경되었을 때의 로직
                updateList()
            }
        }

    }

    private fun ueserInfo(callback: (String) -> Unit) {
        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')

        usersInfoCollectionRef.get().addOnSuccessListener { querySnapshot ->
            var name = ""

            for (doc in querySnapshot) {
                val uid = doc.getString("uid")

                if (userEmail == uid) {
                    name = doc.getString("name") ?: ""
                    break
                }
            }
            callback(name)

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
                        if (userEmail == (it["receiverName"].toString())) {
                            items.add(MsgItem(doc))
                            adapter?.msgUpdateList(items)
                        }
                    }

            }
            adapter?.msgUpdateList(items)
        }
    }
}
//
//
//    private fun updateList() {
//        auth = Firebase.auth
//
//            msgitemsCollectionRef.get().addOnSuccessListener {
//
//                val items = mutableListOf<MsgItem>()
//                for (doc in it) {
//                        if (revname == (doc.getString("receiverName").toString())) {
//                            items.add(MsgItem(doc))
//                            adapter?.msgUpdateList(items)
//
//                    }
//                }
//                adapter?.msgUpdateList(items)
//
//        }
//
//    }

//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.home -> {
//                onBackPressed()  // 뒤로가기 버튼과 동일한 동작을 수행합니다
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    // 툴바 메뉴 버튼을 설정- menu에 있는 item을 연결하는 부분
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
//    }
//}