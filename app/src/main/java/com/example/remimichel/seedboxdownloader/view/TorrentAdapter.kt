package com.example.remimichel.seedboxdownloader.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.remimichel.seedboxdownloader.R
import com.example.remimichel.seedboxdownloader.data.remote.Torrent

class TorrentAdapter(var torrents: List<Torrent> = ArrayList(0)) : RecyclerView.Adapter<TorrentAdapter.ViewHolder>() {
  class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
    var name: TextView = view.findViewById(R.id.file_name)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TorrentAdapter.ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.file, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.name.text = torrents[position].name
  }

  override fun getItemCount() = torrents.count()
}
