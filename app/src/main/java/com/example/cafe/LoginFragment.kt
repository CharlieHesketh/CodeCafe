package com.example.cafe

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)

        username = view.findViewById(R.id.log_username)
        password = view.findViewById(R.id.log_password)
        fAuth = Firebase.auth

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(),false)
        }

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            validateForm()
        }
        return view
    }

    private fun firebaseSignIn(){
        btn_login.isEnabled = false
        btn_login.alpha-0.5f
        fAuth.signInWithEmailAndPassword(username.text.toString(),
        password.text.toString()).addOnCompleteListener{
            task ->
            if (task.isSuccessful){
                var navHome = activity as FragmentNavigation
                navHome.navigateFrag(HomeFragment(), addToStack = true)
            }else{
                btn_login.isEnabled = true
                btn_login.alpha=1.0f
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(){
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning_32)

        icon?.setBounds(0,0,icon.intrinsicWidth,icon.intrinsicHeight)
        when {

            TextUtils.isEmpty(username.text.toString().trim()) ->{
                username.setError("Please Enter Username", icon)
            }
            TextUtils.isEmpty(password.text.toString().trim()) ->{
                password.setError("Please Enter Password", icon)
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() ->
            {
                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){

//                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    firebaseSignIn()

                }else(
                        username.setError("Please enter a valid Email", icon)
                        )
            }

        }

    }


}