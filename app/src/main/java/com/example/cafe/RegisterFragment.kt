package com.example.cafe

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var cnfPassword: EditText
    private lateinit var fAuth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_register, container, false)

        username = view.findViewById(R.id.reg_username)
        password = view.findViewById(R.id.reg_password)
        cnfPassword = view.findViewById(R.id.reg_cnf_password)
        fAuth = Firebase.auth

        view.findViewById<Button>(R.id.btn_login_reg).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(),false)
        }

        view.findViewById<Button>(R.id.btn_register_reg).setOnClickListener{
            validateEmptyForm()
        }
        return view
    }

    private fun firebaseSignup(){
        btn_register_reg.isEnabled = false
        btn_register_reg.alpha = 0.5f
        fAuth.createUserWithEmailAndPassword(username.text.toString(),
            password.text.toString()).addOnCompleteListener{
                task ->
            if(task.isSuccessful) {

                var navHome = activity as FragmentNavigation
                navHome.navigateFrag(HomeFragment(), addToStack = true)

            }else{
                btn_register_reg.isEnabled = true
                btn_register_reg.alpha = 1.0f
                Toast.makeText(context, task.exception?.message, LENGTH_SHORT).show()
            }
        }


    }

    private fun validateEmptyForm(){
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning_32)

        icon?.setBounds(0,0,icon.intrinsicWidth,icon.intrinsicHeight)
        when{
            TextUtils.isEmpty(username.text.toString().trim()) ->{
                username.setError("Please Enter Username", icon)
            }
            TextUtils.isEmpty(password.text.toString().trim()) ->{
                password.setError("Please Enter Password", icon)
            }
            TextUtils.isEmpty(cnfPassword.text.toString().trim()) ->{
                cnfPassword.setError("Please Confirm Password", icon)
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfPassword.text.toString().isNotEmpty() ->
            {
                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){

                    if(password.text.toString().length>=5){

                        if(password.text.toString() == cnfPassword.text.toString()){

                            firebaseSignup()

                        } else{
                            cnfPassword.setError("Please make sure the passwords match", icon)
                        }
                    } else{
                        password.setError("Password needs to be longer than 5 characters", icon)
                    }
                }else(
                        username.setError("Please enter a valid Email", icon)
                )
            }
        }
    }

}