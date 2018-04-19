package com.example.remimichel.seedboxdownloader.view

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.presenter.BaseFragment

class MainActivity : AppCompatActivity(),
    FilesFragment.OnFragmentInteractionListener,
    TorrentsFragment.OnFragmentInteractionListener,
    SettingsFragment.OnFragmentInteractionListener {

  private var mTextMessage: TextView? = null

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
  }

  override fun onBackPressed() {
    val fragmentList = supportFragmentManager.fragments
    var handled = false
    for (f in fragmentList) {
      if (f is BaseFragment) {
        handled = (f as BaseFragment).onBackPressed()

        if (handled) {
          break
        }
      }
    }
    if (!handled) {
      super.onBackPressed()
    }
    //onBackButtonClick(this, getCredentials("server1"))
  }

  override fun onFragmentInteraction(uri: Uri) {}


  fun addFragment(f: Fragment): Boolean {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.frame, f)
    fragmentTransaction.commit()
    return true
  }

}
