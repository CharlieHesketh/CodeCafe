package com.example.cafe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.cafe.R
import com.example.cafe.event.UpdateCart
import com.example.cafe.listener.CartLoadListener
import com.example.cafe.listener.RecyclerClickListener
import com.example.cafe.model.CartModel
import com.example.cafe.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus


class MyProductAdapter(
    private val context: Context,
    private val list:List<ProductModel>,
    private val cartLoadListener: CartLoadListener

    ): RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>(){
    class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
      var imageView:ImageView?=null
      var txtName:TextView?=null
      var txtPrice:TextView?=null

        private var clickListener: RecyclerClickListener? = null

        fun setclickListener(clickListener: RecyclerClickListener){
            this.clickListener=clickListener
        }

     init {
        imageView = itemView.findViewById(R.id.product_image) as ImageView
         txtName = itemView.findViewById(R.id.product_title) as TextView
         txtPrice = itemView.findViewById(R.id.product_price) as TextView

         itemView.setOnClickListener(this)
     }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListner(v,adapterPosition)
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

        holder.setclickListener(object :RecyclerClickListener{
            override fun onItemClickListner(view: View?, position: Int) {
                addToCart(list[position])
            }
        })
    }

    private fun addToCart(productModel: ProductModel) {
        val userCart = FirebaseDatabase.getInstance("https://code-cafe-35503-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("cart")
            .child("uid")

        userCart.child(productModel.key!!)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String,Any> = HashMap()

                        cartModel!!.quantity = cartModel!!.quantity+1
                        updateData["quantity"]= cartModel!!.quantity
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()


                        userCart.child(productModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCart())
                                cartLoadListener.onLoadCartFailed("Added to Cart")
                            }
                            .addOnFailureListener {error -> cartLoadListener.onLoadCartFailed(error.message) }
                    }
                    else{

                        val cartModel = CartModel()
                        cartModel.key = productModel.key
                        cartModel.title = productModel.title
                        cartModel.image = productModel.img
                        cartModel.price = productModel.price
                        cartModel.quantity=1
                        cartModel.totalPrice = productModel.price!!.toFloat()

                        userCart.child(productModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCart())
                                cartLoadListener.onLoadCartFailed("Added to Cart")
                            }
                            .addOnFailureListener {error -> cartLoadListener.onLoadCartFailed(error.message) }


                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}