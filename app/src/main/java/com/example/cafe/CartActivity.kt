package com.example.cafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cafe.adapter.MyCartAdapter
import com.example.cafe.event.UpdateCart
import com.example.cafe.listener.CartLoadListener
import com.example.cafe.model.CartModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartActivity : AppCompatActivity(), CartLoadListener {
    var cartLoadListener:CartLoadListener?=null


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCart::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCart::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true )
    fun onUpdateCart(event: UpdateCart){
        loadCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        init()
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        val cartModels:MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance("https://code-cafe-35503-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("cart")
            .child("uid")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for(cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }

            })
    }

    private fun init(){
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        btnBack!!.setOnClickListener{finish()}
        }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var total = 0.0
        for(cartModel in cartModelList!!){
            total+= cartModel!!.totalPrice
        }
        txtOrderTotal.text = StringBuilder("£").append(total)
        val adapter = MyCartAdapter(this,cartModelList)
        recycler_cart!!.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Toast.makeText(this,message!!, Toast.LENGTH_LONG).show()
    }
}