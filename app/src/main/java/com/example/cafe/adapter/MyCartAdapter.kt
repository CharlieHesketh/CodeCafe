package com.example.cafe.adapter

import android.app.AlertDialog
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.cafe.R
import com.example.cafe.event.UpdateCart
import com.example.cafe.model.CartModel
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus

class MyCartAdapter(
    private val context: Context,
    private val cartModelList:List<CartModel>
):RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {


    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var btnMinus:ImageView?=null
        var btnPlus:ImageView?=null
        var btnDelete:ImageView?=null
        var product_image:ImageView?=null
        var product_title:TextView?=null
        var product_price:TextView?=null
        var txtQuantity:TextView?=null

        init {
            btnMinus= itemView.findViewById(R.id.btnMinus) as ImageView
            btnPlus= itemView.findViewById(R.id.btnPlus) as ImageView
            btnDelete= itemView.findViewById(R.id.btnDelete) as ImageView
            product_image= itemView.findViewById(R.id.product_image) as ImageView
            product_title= itemView.findViewById(R.id.product_title) as TextView
            product_price= itemView.findViewById(R.id.product_price) as TextView
            txtQuantity= itemView.findViewById(R.id.txtQuantity) as TextView
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.product_cart_view,parent,false))
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].image)
            .override(Target.SIZE_ORIGINAL)
            .into(holder.product_image!!)
        holder.product_title!!.text = StringBuilder().append(cartModelList[position].title)
        holder.product_price!!.text = StringBuilder("£").append(cartModelList[position].price)
        holder.txtQuantity!!.text = StringBuilder("").append(cartModelList[position].quantity)

        //events
        holder.btnMinus!!.setOnClickListener { _ -> minusItem(holder, cartModelList[position]) }
        holder.btnPlus!!.setOnClickListener { _ -> plusItem(holder, cartModelList[position]) }
        holder.btnDelete!!.setOnClickListener{_ ->
            val warningDialog = AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Do you want to delete this item?")
                .setNegativeButton("Cancel") {warningDialog,_ -> warningDialog.dismiss()}
                .setPositiveButton("Yes") {warningDialog,_ ->

                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance("https://code-cafe-35503-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("cart")
                        .child("uid")
                        .child(cartModelList[position].key!!)
                        .removeValue()
                        .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCart()) }
                }
                .create()
            warningDialog.show()
        }
    }

    private fun deleteItem(holder: MyCartAdapter.MyCartViewHolder, cartModel: CartModel) {
        cartModel.quantity+=1
        cartModel.totalPrice=cartModel.quantity*cartModel.price!!.toFloat()
        holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
        updateFirebase(cartModel)
    }

    private fun plusItem(holder: MyCartAdapter.MyCartViewHolder, cartModel: CartModel) {
        cartModel.quantity+=1
        cartModel.totalPrice=cartModel.quantity*cartModel.price!!.toFloat()
        holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
        updateFirebase(cartModel)
    }

    private fun minusItem(holder: MyCartAdapter.MyCartViewHolder, cartModel: CartModel) {
        if(cartModel.quantity>1)
        {
            cartModel.quantity-=1
            cartModel.totalPrice=cartModel.quantity*cartModel.price!!.toFloat()
            holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
            updateFirebase(cartModel)
        }
    }

    private fun updateFirebase(cartModel: CartModel) {
        FirebaseDatabase.getInstance("https://code-cafe-35503-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("cart")
            .child("uid")
            .child(cartModel.key!!)
            .setValue(cartModel)
            .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCart())}
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }

}