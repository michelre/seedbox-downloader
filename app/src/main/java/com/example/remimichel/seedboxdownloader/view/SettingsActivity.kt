package com.example.remimichel.seedboxdownloader.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.remimichel.seedboxdownloader.R

class SettingsActivity : AppCompatActivity() {
  private var mTextMessage: TextView? = null
  private lateinit var server1: List<Pair<String, EditText>>

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_home -> {
        //mTextMessage!!.setText(R.string.title_files)
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_dashboard -> {
        //mTextMessage!!.setText(R.string.title_torrents)
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_notifications -> {
        //mTextMessage!!.setText(R.string.title_settings)
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
    server1 = listOf<Pair<String, EditText>>(
        Pair("server1_host", findViewById<EditText>(R.id.server1_host) as EditText),
        Pair("server1_login", findViewById<EditText>(R.id.server1_login) as EditText),
        Pair("server1_password", findViewById<EditText>(R.id.server1_password) as EditText),
        Pair("server2_host", findViewById<EditText>(R.id.server2_host) as EditText),
        Pair("server2_login", findViewById<EditText>(R.id.server2_login) as EditText),
        Pair("server2_password", findViewById<EditText>(R.id.server2_password) as EditText)
    )
    initSettings()
    val saveSettingsBtn = findViewById<Button>(R.id.save_settings) as Button
    saveSettingsBtn.setOnClickListener { saveSettingsAction() }
  }

  fun initSettings(){
    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
    server1.forEach { (k, v) -> v.setText(sharedPreferences.getString(k, "")) }
  }

  fun saveSettingsAction(){
    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()){
      server1.map { putString(it.first, it.second.text.toString()) }
      commit()
    }
  }
}
