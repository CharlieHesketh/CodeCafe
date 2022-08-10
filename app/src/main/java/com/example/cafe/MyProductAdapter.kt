package com.example.cafe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.cafe.ProductModel
import java.lang.StringBuilder


class MyProductAdapter(
    private val context: Context,
    private val list:List<ProductModel>

    ): RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>(){
    class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      var imageView:ImageView?=null
      var txtName:TextView?=null
      var txtPrice:TextView?=null

     init {
        imageView = itemView.findViewById(R.id.product_image) as ImageView
         txtName = itemView.findViewById(R.id.product_title) as TextView
         txtPrice = itemView.findViewById(R.id.product_price) as TextView
     }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.product_card_view,parent,false))
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
       Glide.with(context)
           .load(list[position].img)
           .override(SIZE_ORIGINAL)
           .into(holder.imageView!!)
        holder.txtName!!.text=StringBuilder().append(list[position].title)
        holder.txtPrice!!.text=StringBuilder("£").append(list[position].price)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}