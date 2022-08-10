package com.example.cafe.listener

import com.example.cafe.model.ProductModel

interface ProductLoadListener {
    fun onProductLoadSuccess(productModelList:List<ProductModel>?)
    fun onProductLoadFailed(message:String?)
}