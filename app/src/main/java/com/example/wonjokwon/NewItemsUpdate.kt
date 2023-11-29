package com.example.wonjokwon

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class NewItemsUpdate : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private var adapter: RvAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")
    private val usersInfoCollectionRef = db.collection("UsersInfo")
    private val editPrice by lazy {findViewById<EditText>(R.id.editPrice2)}
    private val editItemName by lazy {findViewById<EditText>(R.id.editItemName)}
    private val ItemStory by lazy {findViewById<EditText>(R.id.Updatestory)}

    private var selectedImageUri: Uri? = null


    override fun onStop() {
        super.onStop()
        updateList()
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_items_update)


        //아래에서 위로 올라옴
        overridePendingTransition(R.anim.from_down_enter, R.anim.none);



        findViewById<Button>(R.id.buttonSelectImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }


        findViewById<Button>(R.id.buttonAddUpdate).setOnClickListener {
            selectedImageUri?.let { uri ->
                updateUserInfoList { name ->
                    addItem(name, uri)
                    updateList()
                }
            } ?: run {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
            updateList()
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    //뒤로가기 버튼 누르면 위에서 아래로 내려감
    override fun onBackPressed() {
        super.onBackPressed();

        if (isFinishing()) {
            overridePendingTransition(R.anim.none, R.anim.to_down_exit);
        }
    }


    private fun updateUserInfoList(callback: (String) -> Unit) {
        val auth = Firebase.auth
        val userEmail = auth.currentUser?.email?.substringBefore('@') ?: ""

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
        itemsCollectionRef.get().addOnSuccessListener {
            val items = mutableListOf<Item>()
            for (doc in it) {
                items.add(Item(doc))
            }
            adapter?.updateList(items)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
        }
    }
    private fun addItem(UserName :String,imageUri: Uri) {
        auth = Firebase.auth

        val username = UserName
        val uid = auth.currentUser?.email?.substringBefore('@') ?: ""

        val name = editItemName.text.toString()
        val story = ItemStory.text.toString()
        val price = editPrice.text.toString().toInt()

        val imageRef = Firebase.storage.reference.child("itemImages/${imageUri.lastPathSegment}")
        val uploadTask = imageRef.putFile(imageUri)

        val status = "unselled"
        if (name.isEmpty()) {
            Snackbar.make(editItemName, "Input name!", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (story.isEmpty()) {
            Snackbar.make(editItemName, "Input story!", Snackbar.LENGTH_SHORT).show()
            return
        }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()

                // Firestore에 아이템 정보와 이미지 URL 저장
                val itemMap = hashMapOf(
                    "uid" to uid,
                    "username" to username,
                    "name" to name,
                    "story" to story,
                    "price" to price,
                    "status" to status,
                    "imageUrl" to downloadUrl  // 이미지 URL 추가
                )
                itemsCollectionRef.document().set(itemMap)
                    .addOnSuccessListener { updateList() }.addOnFailureListener { /* 오류 처리 */ }
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1001
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