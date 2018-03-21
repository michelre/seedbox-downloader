package com.example.remimichel.seedboxdownloader

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.remimichel.seedboxdownloader.data.remote.getTorrents
import com.example.remimichel.seedboxdownloader.data.remote.getXTransmissionSessionId

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
