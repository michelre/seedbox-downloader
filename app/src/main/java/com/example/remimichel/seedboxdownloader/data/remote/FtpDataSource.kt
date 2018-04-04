package com.example.remimichel.seedboxdownloader.data.remote

import android.util.Log
import arrow.core.Either
import arrow.data.Try
import arrow.effects.IO
import arrow.effects.monad
import arrow.syntax.applicativeerror.attempt
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.foldable.fold
import arrow.syntax.functor.map
import arrow.typeclasses.binding
import kotlinx.coroutines.experimental.async
import org.apache.commons.net.ftp.FTPClient

data class File(val name: String, val isDirectory: Boolean)

fun getFtpClient(credentials: Map<String, String>?): FTPClient {
  val ftpClient = FTPClient()
  ftpClient.connect(credentials!!["host"])
  ftpClient.login(credentials!!["login"], credentials["password"])
  ftpClient.enterLocalPassiveMode()
  return ftpClient
}

fun getFilesAndDirectories(credentials: Map<String, String>, basePath: String) =
    IO.async<List<File>> {
      async {
        val ftpClient = getFtpClient(credentials)
        ftpClient.changeWorkingDirectory(basePath)
        it(ftpClient.listFiles().map { File(it.name, it.isDirectory) }.right())
      }
    }


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
}
