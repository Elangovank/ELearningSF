package com.gm.utilities

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import com.core.utils.AppPreferences
import com.gm.GMApplication
import com.gm.R
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.activities.ValidateUserActivity
import com.gm.models.Model
import com.google.gson.Gson

object IntentUtils {
    fun intent(context: Context) {
        if(HomeActivity.homeActivityWeekReference!=null){
            GMApplication.loginUserId = 0
            GMApplication.level2Token = ""
            val bundle = Bundle()
            val user = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.KEY_LOGIN_DATA)
            user?.let {
                val userDetails = Gson().fromJson<Model.LoginData>(it, Model.LoginData::class.java)
                userDetails?.let {
                    bundle.putString(ValidateUserActivity.ARG_FARMER_CODE, it.farmCode ?: "")
                }
            }
            AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_LOGIN_DATA, "")
            AppPreferences.getInstance()?.setLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID, 0)
            val myIntent=Intent(context, ValidateUserActivity::class.java)
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(myIntent, bundle)
        }
    }
    fun assignLanguage(it:Int?):String? {
        var languageToLoad="en"
        if(it==1) languageToLoad="en"
        else if(it==2) languageToLoad="ta"
        else if(it==3) languageToLoad="bn"
        else if(it==4) languageToLoad="gu"
        else if(it==5) languageToLoad="kn"
        else if(it==6) languageToLoad="mr"
        else if(it==7) languageToLoad="hi"
        else if(it==8) languageToLoad="te"
        else languageToLoad="en"
        return languageToLoad

    }

    fun assignErrorLanguageForId(resources:Resources,id:Int?):String?{
        return resources.getStringArray(R.array.error_list)[id?:0]
    }




    fun checkResponseObject(responseObject:String):Boolean
    {
        return try{
           return Integer.parseInt(responseObject)<=175
        }
        catch (e:NumberFormatException)
        {
            false
        }
    }

}