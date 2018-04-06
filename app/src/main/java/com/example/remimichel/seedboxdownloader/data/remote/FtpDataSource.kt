package com.example.remimichel.seedboxdownloader.data.remote

import arrow.data.Try
import arrow.effects.IO
import arrow.effects.monad
import arrow.syntax.foldable.fold
import arrow.typeclasses.binding
import kotlinx.coroutines.experimental.async
import org.apache.commons.net.ftp.FTPClient

data class File(val name: String, val isDirectory: Boolean)

class CommandException(override var message: String) : Exception(message)

fun getFtpClient(credentials: Map<String, String>?): FTPClient {
  val ftpClient = FTPClient()
  ftpClient.connect(credentials!!["host"])
  ftpClient.login(credentials!!["login"], credentials["password"])
  ftpClient.enterLocalPassiveMode()
  return ftpClient
}

fun getFtpClient(credentials: Map<String, String>) = IO.async<Boolean> { callback ->
    val ftpClient = FTPClient()
    ftpClient.connect(credentials["host"])
    val res = runInAsyncMode(ftpClient, command = { ftpClient.login(credentials["login"], credentials["password"]) })
        .map { res ->
          ftpClient.enterLocalPassiveMode()
          res
        }
    res.
}


fun runInAsyncMode(ftpClient: FTPClient, command: () -> Boolean) = IO.async<Boolean> { callback ->
  async {
    val tryCommand = Try { command() }
        .map { success -> if (!success) throw CommandException(ftpClient.replyString) else success }
    callback(tryCommand.toEither())
  }
}


fun getFilesAndDirectories(credentials: Map<String, String>, basePath: String) =
    IO.async<List<File>> { action ->
      async {
        val listTry = Try {
          val ftpClient = getFtpClient(credentials)
          ftpClient.changeWorkingDirectory(basePath)
          ftpClient.listFiles().map { File(it.name, it.isDirectory) }
        }
        action(listTry.toEither())
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
