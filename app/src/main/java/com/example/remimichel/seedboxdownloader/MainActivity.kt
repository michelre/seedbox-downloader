package com.example.remimichel.seedboxdownloader

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import arrow.effects.IO
import com.example.remimichel.seedboxdownloader.data.Torrent
import com.example.remimichel.seedboxdownloader.data.remote.getTorrents
import com.example.remimichel.seedboxdownloader.data.remote.getXTransmissionSessionId
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private var mTextMessage: TextView? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                mTextMessage!!.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                mTextMessage!!.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                mTextMessage!!.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTextMessage = findViewById<View>(R.id.message) as TextView?
        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        getXTransmissionSessionId()
                .unsafeRunAsync {
                    it.fold(
                            { Log.d("TEST", it.message) },
                            {
                                val button = findViewById<View>(R.id.button2)
                                button.setOnClickListener { _ -> onClickButton2Action(it) }
                            })
                }
    }


    fun onClickButton2Action(sessionId: String) {
        getTorrents(sessionId).unsafeRunAsync { result -> result.fold({}, { Log.i("TEST", it.toString()) }) }
    }

}
