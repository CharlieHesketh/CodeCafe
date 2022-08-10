package com.example.cafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*

class MainActivity : AppCompatActivity(), FragmentNavigation, ProductLoadListener {

    lateinit var productLoadListener: ProductLoadListener
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        loadProductFromFirebase()

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

        val gridLayoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,
            false)
        recycler_food.layoutManager = gridLayoutManager
        recycler_food.setHasFixedSize(true)
        recycler_food.addItemDecoration(ItemDecoration())
    }

    override fun onProductLoadSuccess(productModelList: List<ProductModel>?) {
    val adapter = MyProductAdapter(this,productModelList!!)
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
}