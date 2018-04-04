package com.example.remimichel.seedboxdownloader.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.data.remote.File
import com.example.remimichel.seedboxdownloader.data.remote.getFilesAndDirectories
import com.example.remimichel.seedboxdownloader.data.remote.uploadData

class FileFTPAdapter(var files: List<File> = ArrayList(0), val credentials: Map<String, Map<String, String>>, var ctx: Context) : RecyclerView.Adapter<FileFTPAdapter.ViewHolder>() {

  var currentPath: List<String> = listOf("/")

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var name: TextView = view.findViewById(R.id.file_name)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileFTPAdapter.ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.file, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.name.text = files[position].name
    holder.itemView.setOnClickListener {
      val file: File = files[position]
      val toPath = listOf(this.currentPath, listOf(files[position].name)).flatten()
      when (file.isDirectory) {
        true -> navigate(toPath)
        else -> uploadData(toPath, credentials).unsafeRunAsync {
          it.fold({ Log.e("APPP", it.message!!) }, { Toast.makeText(ctx, "FOOOOOO", 100) })
        }
      }
    }
  }

  override fun getItemCount() = files.count()

  fun navigate(path: List<String>) {
    getFilesAndDirectories(credentials["server1"]!!, path.joinToString("/"))
        .unsafeRunAsync {
          it.fold({}, {
            this.files = it
            this.currentPath = path
            this.notifyDataSetChanged()
          })
        }
  }
}
