package com.example.bluetoothserver.Bluetoothchat

import android.util.Log

class AppLog {
    companion object {

        fun d(tag: String, msg: String){
            Log.d(tag,msg)
        }

        fun v(tag: String, msg: String){
            Log.v(tag,msg)
        }

        fun e(tag: String, msg: String){
            Log.e(tag,msg)
        }

    }
}