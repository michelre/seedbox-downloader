package com.example.remimichel.seedboxdownloader.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import android.widget.TextView
import com.example.remimichel.seedboxdownloader.R

class SettingsActivity : AppCompatActivity() {
  private var mTextMessage: TextView? = null

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
    setContentView(R.layout.activity_settings)
    val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
  }
}
