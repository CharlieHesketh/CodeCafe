package com.example.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafe.model.Product
import com.example.cafe.R
import com.squareup.picasso.Picasso
import java.lang.StringBuilder

class ProductAdapter(private var productList: MutableList<Product>):RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val layoutView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.product_card_view, parent, false)
        return ProductViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int){
        Picasso.get().load(productList[position].img)
            .into(holder.productImage)
        holder.productTitle.text=StringBuilder("£").append(productList[position].title)
        holder.productPrice.text=(productList[position].price)
    }

    override fun getItemCount(): Int{
        return productList.size
    }

    class ProductViewHolder(view: View): RecyclerView.ViewHolder(view){
        var productImage: ImageView = view.findViewById(R.id.product_image)
        var productTitle: TextView = view.findViewById(R.id.product_title)
        var productPrice: TextView = view.findViewById(R.id.product_price)
    }
}