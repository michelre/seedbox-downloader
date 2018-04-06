package com.example.remimichel.seedboxdownloader.view

import android.content.Context
import android.content.Intent
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
import com.example.remimichel.seedboxdownloader.presenter.FtpListView
import com.example.remimichel.seedboxdownloader.presenter.getFtpContent
import com.example.remimichel.seedboxdownloader.presenter.onBackButtonClick
import com.example.remimichel.seedboxdownloader.presenter.onFtpListItemClick

class MainActivity : AppCompatActivity(), FtpListView {

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
        //mTextMessage!!.setText(R.string.title_settings)
        val i = Intent(this, SettingsActivity::class.java);
        startActivity(i)
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
    getFtpContent(this, this.getCredentials("server1"))
  }

  override fun onBackPressed() {
    onBackButtonClick(this, getCredentials("server1"))
  }

  override fun displayList(files: List<File>) {
    runOnUiThread {
      this.adapter.files = files
      this.adapter.notifyDataSetChanged()
    }
  }

  override fun drawError(error: Throwable) {
    Log.e("APPP", error.message)
  }

  fun getCredentials(server: String): Map<String, String> {
    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return mapOf(
        "host" to sharedPreferences.getString("${server}_host", ""),
        "login" to sharedPreferences.getString("${server}_login", ""),
        "password" to sharedPreferences.getString("${server}_password", "")
    )
  }

  fun setupFTPList() {
    val recyclerView = findViewById<RecyclerView>(R.id.file_view)
    recyclerView.setHasFixedSize(true)
    adapter = FileFTPAdapter(listOf(), { onFtpListItemClick(this, this.getCredentials("server1"), it) })
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = adapter
  }

}
