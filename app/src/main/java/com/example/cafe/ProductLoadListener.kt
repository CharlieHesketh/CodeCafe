package com.example.cafe

interface ProductLoadListener {
    fun onProductLoadSuccess(productModelList:List<ProductModel>?)
    fun onProductLoadFailed(message:String?)
}