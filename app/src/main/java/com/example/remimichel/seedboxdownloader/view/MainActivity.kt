package com.example.remimichel.seedboxdownloader.view

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.data.remote.File
import com.example.remimichel.seedboxdownloader.data.remote.getFilesAndDirectories

class MainActivity : AppCompatActivity() {

  private var mTextMessage: TextView? = null
  private lateinit var adapter: FileFTPAdapter

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_home -> {
        mTextMessage!!.setText(R.string.title_files)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_dashboard -> {
        mTextMessage!!.setText(R.string.title_torrents)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_notifications -> {
        mTextMessage!!.setText(R.string.title_settings)
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
    setupFTPList()
    getFilesAndDirectories("").unsafeRunAsync { result -> result.fold({ Log.e("APPP", it.message) }, { updateFTPList(it) }) }
  }

  fun setupFTPList() {
    val recyclerView = findViewById<RecyclerView>(R.id.file_view)
    recyclerView.setHasFixedSize(true)
    adapter = FileFTPAdapter()
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = adapter
  }

  fun updateFTPList(files: List<File>) {
    adapter.files = files
    adapter.notifyDataSetChanged()
  }

}
