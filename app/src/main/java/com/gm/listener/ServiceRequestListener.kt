package com.gm.listener

interface ServiceRequestListener {
    fun onRequestCompleted(responseObject: Any?)
    fun onRequestFailed(responseObject: String)
}