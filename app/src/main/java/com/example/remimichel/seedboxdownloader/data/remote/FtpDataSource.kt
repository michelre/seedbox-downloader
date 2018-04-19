package com.example.remimichel.seedboxdownloader.data.remote

import android.util.Log
import arrow.data.Try
import arrow.effects.IO
import kotlinx.coroutines.experimental.async
import org.apache.commons.net.ftp.FTPClient

data class File(val name: String, val isDirectory: Boolean)

class CommandException(override var message: String) : Exception(message)

fun <A, B> runFtpCommand(ftp: FTPClient, command: (FTPClient) -> A, onSuccess: (A) -> B): IO<B> {
  return IO.async { callback ->
    async {
      val res = Try { Log.d("APPP", ftp.replyString ?: ""); command(ftp) }
          .map({ res ->
            if (res is Boolean && !res) {
              throw CommandException(ftp.replyString)
            }
            onSuccess(res)
          })
      callback(res.toEither())
    }
  }
}

fun connect(credentials: Map<String, String>): IO<FTPClient> {
  val ftp = FTPClient()
  return runFtpCommand(ftp, { it.connect(credentials["host"]) }, {})
      .flatMap { runFtpCommand(ftp, { it.login(credentials["login"], credentials["password"]) }, { ftp }) }
      .flatMap { runFtpCommand(ftp, { it.enterLocalPassiveMode() }, { ftp }) }
}

fun getFilesAndDirectories(ftp: FTPClient, basePath: String): IO<List<File>> {
      return runFtpCommand(ftp, { it.changeWorkingDirectory(basePath) }, { })
      .flatMap { runFtpCommand(ftp, { it.listFiles() }, { it.map { File(it.name, it.isDirectory) } }) }
}

/*
fun uploadData(filePath: List<String>, credentials: Map<String, Map<String, String>>) = IO.async<Boolean> { callback ->
  async {
    val filePathStr = filePath.joinToString("/")
    val fromFTPClient = getFtpClient(credentials = credentials["server1"])
    val toFTPClient = getFtpClient(credentials = credentials["server2"])
    val res = IO.monad().binding {
      val isDirectoriesCreated = createDirectoriesToLocation(filePath.subList(1, filePath.size - 1), toFTPClient).unsafeRunSync()
      if (isDirectoriesCreated) {
        Try { toFTPClient.storeFile(filePathStr, fromFTPClient.retrieveFileStream(filePathStr)) }
      }
      Try.pure(false)
    }.fold().toEither()
    callback(res)
  }
}

fun createDirectoriesToLocation(directories: List<String>, ftpClient: FTPClient): IO<Boolean> {
  val directoryExists = ftpClient.changeWorkingDirectory(directories.joinToString("/"))
  val createdDirectoriesRes = directories.mapIndexed { index, pathPart ->
    val fullPath = directories.take(index).joinToString("/")
    ftpClient.makeDirectory("$fullPath/$pathPart")
  }
  when (createdDirectoriesRes.find { !it }) {
    null -> return IO.pure(true)
    else -> return IO.pure(directoryExists)
  }
}*/
