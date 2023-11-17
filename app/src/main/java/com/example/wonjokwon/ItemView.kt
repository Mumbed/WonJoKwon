package com.example.wonjokwon

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var uid=""
    private var adapter: RvAdapter? = null

    val title=view?.findViewById<TextView>(R.id.ItemTitle)
    val price=view?.findViewById<TextView>(R.id.ItemPrice)
    val story=view?.findViewById<TextView>(R.id.ItemStory)
    val sellerid=view?.findViewById<TextView>(R.id.sellerid)
    val status=view?.findViewById<TextView>(R.id.status)

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


        val Fab=view.findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        val data: String? = arguments?.getString("key")

        fun QueryList(itemID: String){
            itemsCollectionRef.document(itemID.toString()).get().addOnSuccessListener {

                title?.setText("상품명 : "+it["name"].toString())
                price?.setText("가격 : "+it["price"].toString())
                story?.setText("상품설명 : "+it["story"].toString())

                if(it["status"].toString().equals("selled")){
                    statustext?.setText("판매완료")
                }
                else {
                    statustext?.setText("판매중")
                }
                uid=it["uid"].toString()
                sellerid?.setText(it["uid"].toString()+" 님의 판매글")
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

                if(userEmail.equals(uid)){
                    Toast.makeText(context,"판매글을 수정해보에요!!.", Toast.LENGTH_SHORT).show()
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

                        if(status.isChecked){//판매완료
                            itemsCollectionRef.document(data.toString()).update("status", "selled")
                                .addOnSuccessListener { updateList() }
                        }else{
                            itemsCollectionRef.document(data.toString()).update("status", "unselled")
                                .addOnSuccessListener { updateList() }
                        }

                        itemsCollectionRef.document(data.toString()).update("name", editTitle.text.toString())
                            .addOnSuccessListener { updateList() }

                        itemsCollectionRef.document(data.toString()).update("price", editPrice.text.toString())
                            .addOnSuccessListener { updateList() }

                        itemsCollectionRef.document(data.toString()).update("story",editStory.text.toString())
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

            btnMenuItem2.setOnClickListener {

            }

        }

        return view
    }


    companion object {

    }
}