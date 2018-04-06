package com.example.remimichel.seedboxdownloader.presenter

import android.util.Log
import com.example.remimichel.seedboxdownloader.data.remote.File
import com.example.remimichel.seedboxdownloader.data.remote.getFilesAndDirectories

interface FtpListView {
  fun displayList(files: List<File>)
  fun drawError(error: Throwable)
}

data class StateView(var currentPath: List<String> = listOf("/"))
val stateView = StateView()

fun getFtpContent(view: FtpListView, credentials: Map<String, String>) {
  getFilesAndDirectories(credentials, stateView.currentPath.joinToString("/"))
      .unsafeRunAsync { result -> result.fold({ view.drawError(it) }, { view.displayList(it) }) }
}

fun onFtpListItemClick(view: FtpListView, credentials: Map<String, String>, item: File) {
  when(item.isDirectory) {
    true -> {
      stateView.currentPath = listOf(stateView.currentPath, listOf(item.name)).flatten()
      getFtpContent(view, credentials)
    }
    false -> Log.d("APPP", item.name)
  }
}

fun onBackButtonClick(view: FtpListView, credentials: Map<String, String>) {
  stateView.currentPath = stateView.currentPath.dropLast(1)
  getFtpContent(view, credentials)
}