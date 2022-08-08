package com.example.cafe

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    var adapter: ProductAdapter? = null
    var recyclerView:RecyclerView? = null
    var productList = ArrayList<Product>()
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       var view = inflater.inflate(R.layout.fragment_home, container, false)

        database = FirebaseDatabase.getInstance("https://code-cafe-35503-default-rtdb.europe-west1.firebasedatabase.app")
        reference = database?.getReference("products")

        val firebaseListener = object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("test", snapshot.toString())
                productList.clear()

                val child = snapshot.children
                    child.forEach{
                        var products = Product(it.child("img").value.toString(),
                            it.child("price").value.toString(),
                            it.child("title").value.toString())

                        productList.add(products)
                    }
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("test", error.message)
            }
        }
        reference?.addValueEventListener(firebaseListener)


        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = GridLayoutManager(context, 2,
        GridLayoutManager.VERTICAL,
        false)

        adapter = ProductAdapter(productList)
        recyclerView?.adapter = adapter


        view.findViewById<Button>(R.id.btn_log_out).setOnClickListener{
            Firebase.auth.signOut()
            var navLogin = activity as FragmentNavigation
            navLogin.navigateFrag(LoginFragment(), addToStack = false)
        }
        return view
    }


}