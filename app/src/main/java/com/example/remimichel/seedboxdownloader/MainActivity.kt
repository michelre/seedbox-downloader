package com.example.remimichel.seedboxdownloader

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson

data class Torrent(val id: Int, val name: String)

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
        button.setOnClickListener { _ -> onClickButton2Action() }

    }


    fun onClickButton2Action() {
        val data: List<Pair<String, Any>> = listOf("method" to "torrent-get")
        val request = Fuel.post("/", data)
        request.responseString { req, res, result ->
            getResult(res, result, request).fold(success = { data ->
                Log.d("RES API", data.toString())
                data
            }, failure = {
                Log.d("ERROR", it.toString())
            })
        }
    }

    fun getResult(response: Response, result: Result<String, FuelError>, baseRequest: Request): Result<List<Torrent>, FuelError> {
        if (response.statusCode == 409) {
            val headers = mutableMapOf<String, String>()
            headers += Pair("X-Transmission-Session-Id", response.headers["X-Transmission-Session-Id"].toString())
            headers += FuelManager.instance.baseHeaders!!
            FuelManager.instance.baseHeaders = headers
            return deserializeTorrents(baseRequest.responseString().third)
        }
        return deserializeTorrents(result)
    }

    fun deserializeTorrents(result: Result<String, FuelError>): Result<List<Torrent>, FuelError> {
        return result.map { Gson().fromJson<List<Torrent>>(it) }
    }

    fun createFuelManagerInstance() {
        FuelManager.instance.basePath = "http://10.0.2.2:9091/transmission/rpc"
        FuelManager.instance.baseHeaders = mapOf(
                "Authorization" to "Basic dHJhbnNtaXNzaW9uOnRyYW5zbWlzc2lvbg==",
                "Content-Type" to "application/json"
        )
    }

}
