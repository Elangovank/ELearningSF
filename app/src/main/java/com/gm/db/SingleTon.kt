package com.gm.db

import com.gm.GMApplication
import com.gm.models.Model
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File


object SingleTon {


    private var map=HashMap<String, String>()
    private fun readThefile() {
        val gson = Gson()
// val map: MutableMap<String, String> = HashMap()
        if (File(GMApplication.appContext?.externalCacheDir?.getPath().toString().toString() + "/fileName.txt").exists()) {
            val bufferedReader: BufferedReader = File(GMApplication.appContext?.externalCacheDir?.getPath().toString().toString() + "/fileName.txt").bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            val post = gson?.fromJson(inputString, Model.ResourceStringResponse::class.java)
            post.response?.forEach {
                it.value?.let { it1 -> it.key?.let { it2 -> map?.put(it2, it1) } }
            }
        }


//return map as HashMap<String, String>
    }

    fun clearMapValue() {
        map?.clear()
    }


    fun getResourceStringValue(str: String): String {
        var keyString: String? = null
        if (map.isNullOrEmpty()) {
            readThefile()
        }
        if (map?.size != null) {
            if (map?.containsKey(str)!!) {
                keyString = map?.get(str)
            } else {
                keyString = "-"
            }

        }
        return keyString ?: "-"
    }


}