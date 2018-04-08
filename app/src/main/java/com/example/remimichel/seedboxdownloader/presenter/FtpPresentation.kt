package com.example.remimichel.seedboxdownloader.presenter

import android.util.Log
import com.example.remimichel.seedboxdownloader.data.remote.File
import com.example.remimichel.seedboxdownloader.data.remote.connect
import com.example.remimichel.seedboxdownloader.data.remote.getFilesAndDirectories
import org.apache.commons.net.ftp.FTPClient

interface FtpListView {
  fun displayList(files: List<File>)
  fun drawError(error: Throwable)
}

data class StateView(var currentPath: List<String> = listOf("/"), var ftp: FTPClient = FTPClient())

val stateView = StateView()

fun connectFtpServer(credentials: Map<String, String>) = connect(credentials)

fun getFtpContent(view: FtpListView, ftp: FTPClient) {
  getFilesAndDirectories(ftp, stateView.currentPath.joinToString("/"))
      .unsafeRunAsync { result -> result.fold({ view.drawError(it) }, { view.displayList(it) }) }
}

fun initFtpContent(view: FtpListView, credentials: Map<String, String>) {
  connectFtpServer(credentials)
      .unsafeRunAsync { ftp ->
        ftp.fold(
            { view.drawError(it) },
            { stateView.ftp = it; getFtpContent(view, it) })
      }
}

fun onFtpListItemClick(view: FtpListView, item: File) {
  when (item.isDirectory) {
    true -> {
      stateView.currentPath = listOf(stateView.currentPath, listOf(item.name)).flatten()
      getFtpContent(view, stateView.ftp)
    }
    false -> Log.d("APPP", item.name) /* uploadData(toPath, credentials).unsafeRunAsync {
          it.fold({ Log.e("APPP", it.message!!) }, { Toast.makeText(ctx, "FOOOOOO", 100) }) */
  }
}

fun onBackButtonClick(view: FtpListView, credentials: Map<String, String>) {
  stateView.currentPath = stateView.currentPath.dropLast(1)
  getFtpContent(view, stateView.ftp)
}