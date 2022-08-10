package com.example.cafe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafe.adapter.ProductAdapter
import com.example.cafe.model.Product
import com.example.cafe.utils.ItemDecoration
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    var recyclerView:RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       var view = inflater.inflate(R.layout.activity_main, container, false)


        view.findViewById<Button>(R.id.btn_log_out).setOnClickListener{
            Firebase.auth.signOut()
            var navLogin = activity as FragmentNavigation
            navLogin.navigateFrag(LoginFragment(), addToStack = false)
        }
//        view.findViewById<Button>(R.id.btn_Cart).setOnClickListener{
//            Firebase.auth.signOut()
//            var navLogin = activity as FragmentNavigation
//            navLogin.navigateFrag(MainActivity(), addToStack = true)
//        }

        return view
    }
}