package com.example.remimichel.seedboxdownloader.view

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.data.remote.getTorrents
import com.example.remimichel.seedboxdownloader.data.remote.getXTransmissionSessionId

class MainActivity : AppCompatActivity() {

  private var mTextMessage: TextView? = null
  private lateinit var adapter: TorrentAdapter

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
    val button = findViewById<Button>(R.id.button2)
    setupList()
    button.setOnClickListener { _ -> onClickButton2Action() }
  }

  fun setupList() {
    val recyclerView = findViewById<RecyclerView>(R.id.torrent_view)
    recyclerView.setHasFixedSize(true)
    adapter = TorrentAdapter()
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = adapter
  }


  fun onClickButton2Action() {
    getXTransmissionSessionId()
        .flatMap { getTorrents(it) }
        .unsafeRunAsync { result ->
          result.fold(
              { Log.e("APP", it.message) },
              {
                adapter.torrents = it
                adapter.notifyDataSetChanged()
              })
        }
  }

}