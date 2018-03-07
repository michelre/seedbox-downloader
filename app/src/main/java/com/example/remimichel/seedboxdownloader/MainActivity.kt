package com.example.remimichel.seedboxdownloader

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import org.json.JSONObject

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

        createFuelManagerInstance()
        val button = findViewById<View>(R.id.button2)
        button.setOnClickListener { _ ->  onClickButton2Action() }

    }


    fun onClickButton2Action(){
        val data: List<Pair<String, Any>> = listOf("method" to "torrent-get")
        Fuel.post("/", data).response { req, res, result  -> Log.d("RES API", result.toString())}
    }

    fun createFuelManagerInstance() {
        FuelManager.instance.basePath = "http://10.0.2.2:9091/transmission/rpc"
        FuelManager.instance.baseHeaders = mapOf(
                "Authorization" to "Basic dHJhbnNtaXNzaW9uOnRyYW5zbWlzc2lvbg==",
                "Content-Type" to "application/json",
                "X-Transmission-Session-Id" to "0MccShiB7ybzFZsrDna6O5HGqDMeFtPrLHXyeCEusxLoa19Z"
        )
    }

}
