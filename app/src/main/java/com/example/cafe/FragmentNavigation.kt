package com.example.cafe

import androidx.fragment.app.Fragment

interface FragmentNavigation {
    fun navigateFrag(fragment: Fragment, addToStack: Boolean)
}