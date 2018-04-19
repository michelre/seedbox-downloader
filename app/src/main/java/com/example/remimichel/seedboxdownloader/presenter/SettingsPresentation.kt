package com.example.remimichel.seedboxdownloader.presenter

import android.content.SharedPreferences

fun saveSetting(sharedPreferences: SharedPreferences, value: Pair<String, String>){
  //val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
  with(sharedPreferences.edit()){
    putString(value.first, value.second)
    commit()
  }
}