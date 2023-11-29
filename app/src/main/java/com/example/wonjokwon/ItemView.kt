package com.example.wonjokwon

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ItemView : Fragment() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")
    private lateinit var auth: FirebaseAuth
    private var username=""
    private var uid=""

    private var adapter: RvAdapter? = null
    private val msgitemsCollectionRef = db.collection("msg")
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

    override fun onResume() {
        super.onResume()
        updateList()
    }

    override fun onStop() {
        super.onStop()
        updateList()
    }


    private fun updateUserInfoList(callback: (String) -> Unit) {
        val auth = Firebase.auth
        val userEmail = auth.currentUser?.email?.substringBefore('@') ?: ""

        usersInfoCollectionRef.get().addOnSuccessListener { querySnapshot ->
            var name = ""

            for (doc in querySnapshot) {
                val uid = doc.getString("uid")

                if (userEmail == uid) {
                    name = doc.getString("uid") ?: ""
                    break
                }
            }

            callback(name)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_item_view, container, false)
        val title=view.findViewById<TextView>(R.id.ItemTitle)
        val price=view.findViewById<TextView>(R.id.ItemPrice)
        val story=view.findViewById<TextView>(R.id.ItemStory)
        val sellerid=view.findViewById<TextView>(R.id.sellerid)
        val statustext=view.findViewById<TextView>(R.id.status)
        val imageView = view.findViewById<ImageView>(R.id.itemImageView)


        val Fab=view.findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        val data: String? = arguments?.getString("key")

        fun QueryList(itemID: String){
            itemsCollectionRef.document(itemID.toString()).get().addOnSuccessListener {
                val imageUrl = it["imageUrl"].toString()  // Firestore에서 이미지 URL 가져오기

                // ImageView에 이미지 로딩
                val imageView = view?.findViewById<ImageView>(R.id.itemImageView)
                context?.let { ctx ->
                    if (imageView != null) {
                        Glide.with(ctx).load(imageUrl).into(imageView)
                    }
                }
                title?.setText("상품명 : "+it["name"].toString())
                price?.setText("가격 : "+it["price"].toString())
                story?.setText("상품설명 : "+it["story"].toString())

                if(it["status"].toString().equals("selled")){
                    statustext?.setText("판매완료")
                }
                else {
                    statustext?.setText("판매중")
                }
                username=it["username"].toString()
                sellerid?.setText(it["username"].toString()+" 님의 판매글")
                uid=it["uid"].toString()
            }.addOnFailureListener {

            }


        }


        QueryList(data.toString())

        updateList()

        Fab.setOnClickListener {

            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout2, null)

            // BottomSheetDialog에 표시할 내용을 설정
            // 여기에 여러 메뉴나 내용을 추가하면 됩니다.
            val btnMenuItem1 = view.findViewById<Button>(R.id.ItemFix)
            val btnMenuItem2 = view.findViewById<Button>(R.id.chattingToSeller)

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            btnMenuItem1.setOnClickListener {
                auth = Firebase.auth
                val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')
                updateUserInfoList { name ->
                    // 이곳에서 name을 사용하거나 처리할 작업을 수행

                    println("User name: $name")


                    if (userEmail == uid) {
                        Toast.makeText(context, "판매글을 수정해보세요!!.", Toast.LENGTH_SHORT).show()
                        val builder = AlertDialog.Builder(context)
                        val inflater = layoutInflater.inflate(R.layout.dialogeditpost, null)
                        builder.setView(inflater)

                        val editTitle = inflater.findViewById<EditText>(R.id.editTitle)
                        val editPrice = inflater.findViewById<EditText>(R.id.editPrice)
                        val editStory = inflater.findViewById<EditText>(R.id.editStory)

                        val buttonSave = inflater.findViewById<Button>(R.id.buttonSave)
                        val buttondelet = inflater.findViewById<Button>(R.id.buttondelet)
                        val status = inflater.findViewById<CheckBox>(R.id.status)
                        editTitle.setText(title.text.toString().substringAfter(": "))
                        editPrice.setText(price.text.toString().substringAfter(": "))
                        editStory.setText(story.text.toString().substringAfter(": "))

                        status.isChecked = statustext.text.toString() != "판매중"
                        status.isChecked = statustext.text.toString() == "판매완료"


                        // 기존 판매글 내용을 가져와서 EditText에 설정

                        val dialog = builder.create()

                        buttonSave.setOnClickListener {

                            if (status.isChecked) {//판매완료
                                itemsCollectionRef.document(data.toString())
                                    .update("status", "selled")
                                    .addOnSuccessListener { updateList() }
                            } else {
                                itemsCollectionRef.document(data.toString())
                                    .update("status", "unselled")
                                    .addOnSuccessListener { updateList() }
                            }

                            itemsCollectionRef.document(data.toString())
                                .update("name", editTitle.text.toString())
                                .addOnSuccessListener { updateList() }

                            itemsCollectionRef.document(data.toString())
                                .update("price", editPrice.text.toString())
                                .addOnSuccessListener { updateList() }

                            itemsCollectionRef.document(data.toString())
                                .update("story", editStory.text.toString())
                                .addOnSuccessListener { updateList() }


                            QueryList(data.toString())

                            updateList()


                            // 다이얼로그를 닫음
                            dialog.dismiss()


                        }
                        buttondelet.setOnClickListener {
                            itemsCollectionRef.document(data.toString()).delete()
                                .addOnSuccessListener { updateList() }

                            updateList()

                            dialog.dismiss()
                        }

                        dialog.show()


                    }
                    else{
                        Toast.makeText(context,"판매자만 수정가능합니다.", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            btnMenuItem2.setOnClickListener {//메세지 전송 로직
                val builder2 = AlertDialog.Builder(context)
                val inflater2 = layoutInflater.inflate(R.layout.msgdialog, null)
                builder2.setView(inflater2)

                val msg=inflater2.findViewById<EditText>(R.id.msgText)
                val sendBtn=inflater2.findViewById<Button>(R.id.buttonSend)
                val titlename=title.text.toString().substringAfter(": ")
                inflater2.findViewById<TextView>(R.id.msgDialogTitle).setText(username+"님에게 메세지 보내기")
                val dialog2 = builder2.create()

                sendBtn.setOnClickListener {
                    addItem(uid,titlename,msg.text.toString())

                }
                dialog2.dismiss()
                dialog2.show()



            }

        }

        return view
    }


    private fun ueserInfo(callback: (String) -> Unit){
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

    private fun addItem(receiverName:String,itemName:String,msgsendtext:String) {
        auth = Firebase.auth
        val receiverName=receiverName
        val itemName=itemName
        val msg=msgsendtext
        if (msg.isEmpty()) {
            view?.let { Snackbar.make(it, "Input text!", Snackbar.LENGTH_SHORT).show() }
            return
        }
        ueserInfo{name ->


            val itemMap = hashMapOf(
                "name" to name,
                "receiverName" to receiverName,
                "itemName" to itemName,
                "msg" to msg
            )
            msgitemsCollectionRef.document().set(itemMap)
                .addOnSuccessListener {
                }.addOnFailureListener {  }


        }



    }


    companion object {

    }
}