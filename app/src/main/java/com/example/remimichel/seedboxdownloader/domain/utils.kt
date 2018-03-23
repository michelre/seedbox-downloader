package com.example.remimichel.seedboxdownloader.domain

import java.text.DecimalFormat

fun getReadableFilesize(size: Long): String {
  if (size <= 0) return "0"
  val units = arrayOf("B", "kB", "MB", "GB", "TB")
  val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
  return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}
