package com.example.wonjokwon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class MsgItem(val id: String, val name: String,val msg:String,val receiverName:String,val itemName:String,val reply:String) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["name"].toString(), doc["msg"].toString(), doc["receiverName"].toString(), doc["itemName"].toString(), doc["reply"].toString())
    constructor(key: String, map: Map<*, *>) :
            this(key, map["name"].toString(), map["msg"].toString(), map["receiverName"].toString(), map["itemName"].toString(), map["reply"].toString())
}

class MyMsgViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class MsgRvAdapter(val context: Context, private var items: List<MsgItem>):
    RecyclerView.Adapter<MyViewHolder> () {
    private lateinit var auth: FirebaseAuth

    fun interface OnItemClickListener {
        fun onItemClick(position: String)
    }
    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    fun msgUpdateList(newList: List<MsgItem>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.msgitems,parent,false)

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        auth = Firebase.auth

        val userEmail = auth.currentUser!!.getEmail().toString().substringBefore('@')
        if(item.receiverName==userEmail) {
            holder.view.findViewById<TextView>(R.id.msgtext).text = item.msg
            if(item.reply=="reply"){
                holder.view.findViewById<TextView>(R.id.sender).text = "<" + item.itemName + ">" +" 상품에 대한 "+item.name+"님의 답장 :"

            }
            else{
                holder.view.findViewById<TextView>(R.id.sender).text = "<" + item.itemName + ">" +" 상품에 대한 "+item.name+"님의 메세지 :"

            }

            holder.view.findViewById<TextView>(R.id.sender).setOnClickListener{
                itemClickListener?.onItemClick(item.id)//키값으로 들어갈 아이디

            }
            holder.view.findViewById<TextView>(R.id.msgtext).setOnClickListener{
                itemClickListener?.onItemClick(item.id)//키값으로 들어갈 아이디

            }
        }




    }


    override fun getItemCount(): Int {
        return items.size
    }
}
