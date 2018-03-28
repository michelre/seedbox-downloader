package com.example.remimichel.seedboxdownloader.data.remote

import arrow.effects.IO
import arrow.syntax.either.right
import kotlinx.coroutines.experimental.async
import org.apache.commons.net.ftp.FTPClient

val seedboxFTPUsername = ""
val seedboxFTPPassword = ""
val seedboxFTPEndpoint = ""

data class File(val name: String)

fun getFilesAndDirectories(basePath: String) =
    IO.async<List<File>> {
      async {
        val ftpClient = FTPClient()
        ftpClient.connect(seedboxFTPEndpoint)
        ftpClient.login(seedboxFTPUsername, seedboxFTPPassword)
        ftpClient.enterLocalPassiveMode()
        it(ftpClient.listFiles(basePath).map { File(it.name) }.right())
      }
    }
