package com.example.cafe.listener

import com.example.cafe.model.CartModel

interface CartLoadListener {
    fun onLoadCartSuccess(cartModelList: List<CartModel>)
    fun onLoadCartFailed(message:String?)
}