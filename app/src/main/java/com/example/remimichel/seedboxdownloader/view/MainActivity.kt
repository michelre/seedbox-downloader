package com.example.remimichel.seedboxdownloader.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.data.remote.File
import com.example.remimichel.seedboxdownloader.presenter.FtpListView
import com.example.remimichel.seedboxdownloader.presenter.initFtpContent
import com.example.remimichel.seedboxdownloader.presenter.onBackButtonClick

class MainActivity : AppCompatActivity(),
    FtpListView,
    FilesFragment.OnFragmentInteractionListener,
    TorrentsFragment.OnFragmentInteractionListener,
    SettingsFragment.OnFragmentInteractionListener {

  private var mTextMessage: TextView? = null
  private lateinit var adapter: FileFTPAdapter

  //https://android.jlelse.eu/ultimate-guide-to-bottom-navigation-on-android-75e4efb8105f

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    mTextMessage = findViewById<View>(R.id.message) as TextView?
    val navigation = findViewById<View>(R.id.bottom_navigation) as AHBottomNavigation
    navigation.addItems(listOf(
        AHBottomNavigationItem(getString(R.string.title_files), R.drawable.ic_home_black_24dp),
        AHBottomNavigationItem(getString(R.string.title_torrents), R.drawable.ic_home_black_24dp),
        AHBottomNavigationItem(getString(R.string.title_settings), R.drawable.ic_home_black_24dp)
    ))
    addFragment(FilesFragment())
    navigation.setOnTabSelectedListener { position, _ ->
      when (position) {
        0 -> addFragment(FilesFragment())
        1 -> addFragment(TorrentsFragment())
        2 -> addFragment(SettingsFragment())
        else -> true
      }
    }
    setupFTPList()
    initFtpContent(this, this.getCredentials("server1"))
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
    Log.e("APPP", "", error)
  }

  override fun onFragmentInteraction(uri: Uri) {}

  fun getCredentials(server: String): Map<String, String> {
    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return mapOf(
        "host" to sharedPreferences.getString("${server}_host", ""),
        "login" to sharedPreferences.getString("${server}_login", ""),
        "password" to sharedPreferences.getString("${server}_password", "")
    )
  }

  fun setupFTPList() {
    /*val recyclerView = findViewById<RecyclerView>(R.id.file_view)
    recyclerView.setHasFixedSize(true)
    adapter = FileFTPAdapter(listOf(), { onFtpListItemClick(this, it) })
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = adapter*/
  }

  fun addFragment(f: Fragment): Boolean {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.frame, f)
    fragmentTransaction.commit()
    return true
  }

}
