package com.example.wonjokwon

import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.QueryDocumentSnapshot


data class Item(
    val id: String,
    val name: String,
    val story: String,
    val status: String,
    val price: Int,
    val imageUrl: String // 이미지 URL 필드 추가
) {
    constructor(doc: QueryDocumentSnapshot) :
            this(
                doc.id,
                doc["name"].toString(),
                doc["story"].toString(),
                doc["status"].toString(),
                doc["price"].toString().toIntOrNull() ?: 0,
                doc["imageUrl"].toString() // Firestore 문서에서 imageUrl 필드 읽기
            )

}

class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class RvAdapter(val context: Context, private var items: List<Item>):
    RecyclerView.Adapter<MyViewHolder> () {
    fun interface OnItemClickListener {
        fun onItemClick(position: String)
    }
    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    fun updateList(newList: List<Item>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.rvitem,parent,false)

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        val imageView = holder.view.findViewById<ImageView>(R.id.rvimageArea)
        Glide.with(context).load(item.imageUrl).into(imageView)
        //holder.view.findViewById<ImageView>(R.id.rvimageArea). = item.id
        holder.view.findViewById<TextView>(R.id.rvTextArea).text = item.name
        holder.view.findViewById<TextView>(R.id.rvTextArea2).text = item.price.toString()
        if(item.status.equals("unselled")){
            holder.view.findViewById<TextView>(R.id.rvTextArea3).text="판매중"
        }
        else{
            holder.view.findViewById<TextView>(R.id.rvTextArea3).text="판매완료"

        }

        /* holder.view.findViewById<TextView>(R.id.textID).setOnClickListener {
             //AlertDialog.Builder(context).setMessage("You clicked ${student.name}.").show()
             itemClickListener?.onItemClick(item.id)
         }*/
        holder.view.findViewById<TextView>(R.id.rvTextArea).setOnClickListener {
            //AlertDialog.Builder(context).setMessage("You clicked ${student.name}.").show()
            itemClickListener?.onItemClick(item.id)//키값으로 들어갈 아이디
        }
        holder.view.findViewById<TextView>(R.id.rvTextArea2).setOnClickListener {
            //AlertDialog.Builder(context).setMessage("You clicked ${student.name}.").show()
            itemClickListener?.onItemClick(item.id)//키값으로 들어갈 아이디
        }
        holder.view.findViewById<TextView>(R.id.rvTextArea3).setOnClickListener {
            //AlertDialog.Builder(context).setMessage("You clicked ${student.name}.").show()
            itemClickListener?.onItemClick(item.id)//키값으로 들어갈 아이디
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }
}
