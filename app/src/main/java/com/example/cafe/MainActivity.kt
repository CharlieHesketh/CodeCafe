package com.example.cafe

import android.media.metrics.Event
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cafe.adapter.MyProductAdapter
import com.example.cafe.event.UpdateCart
import com.example.cafe.listener.CartLoadListener
import com.example.cafe.listener.ProductLoadListener
import com.example.cafe.model.CartModel
import com.example.cafe.model.ProductModel
import com.example.cafe.utils.ItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), FragmentNavigation, ProductLoadListener, CartLoadListener {

    lateinit var productLoadListener: ProductLoadListener
    lateinit var cartLoadListener: CartLoadListener
    private lateinit var fAuth: FirebaseAuth

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
    public fun onUpdateCart(event:UpdateCart){
        totalCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        loadProductFromFirebase()
        totalCartFromFirebase()

//        fAuth = Firebase.auth
//
//        val currentUser = fAuth.currentUser
//
//        if(currentUser != null){
//            supportFragmentManager.beginTransaction()
//                .add(R.id.container, HomeFragment()).addToBackStack(null)
//                .commit()
//        }else {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.container, LoginFragment())
//                .commit()
//        }
    }

    private fun totalCartFromFirebase() {
        val cartModels:MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance("https://code-cafe-35503-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("cart")
            .child("uid")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for(cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    private fun loadProductFromFirebase() {
        val productModels : MutableList<ProductModel> = ArrayList()
        FirebaseDatabase.getInstance("https://code-cafe-35503-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("products")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (productSnapshot in snapshot.children){
                            val productModel = productSnapshot.getValue(ProductModel::class.java)
                            productModel!!.key = productSnapshot.key
                            productModels.add(productModel)
                        }
                        productLoadListener.onProductLoadSuccess(productModels)
                    } else {

                        productLoadListener.onProductLoadFailed("Items don't exist")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    productLoadListener.onProductLoadFailed(error.message)
                }

            })
    }

    private fun init(){
        productLoadListener=this
        cartLoadListener=this

        val gridLayoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,
            false)
        recycler_food.layoutManager = gridLayoutManager
        recycler_food.setHasFixedSize(true)
        recycler_food.addItemDecoration(ItemDecoration())
    }

    override fun onProductLoadSuccess(productModelList: List<ProductModel>?) {
    val adapter = MyProductAdapter(this,productModelList!!, cartLoadListener)
        recycler_food.adapter=adapter

    }

    override fun onProductLoadFailed(message: String?) {
        Toast.makeText(this,message!!,Toast.LENGTH_LONG).show()
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)

        if(addToStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartTotal = 0
        for (cartModel in cartModelList!!) cartTotal+= cartModel!!.quantity
        badge!!.setNumber(cartTotal)
    }

    override fun onLoadCartFailed(message: String?) {
        Toast.makeText(this,message!!,Toast.LENGTH_LONG).show()
    }
}