package com.example.remimichel.seedboxdownloader.data.remote

import android.util.Log
import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import arrow.effects.IO
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.functor.map
import com.example.remimichel.seedboxdownloader.data.Torrent
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject

sealed class HTTPException {
    object Confilct409 : HTTPException()
}

fun getTorrentGetQuery() = jsonObject(
        "method" to "torrent-get",
        "arguments" to jsonObject("fields" to jsonArray("id", "name"))).toString()

fun getFuelRequest(sessionId: String): Request = if (sessionId == "") {
    Fuel.post("http://213.251.183.154:12754/transmission/rpc")
            .header("Authorization" to "Basic")
            .body(getTorrentGetQuery())
            .responseString { req, res, result -> result.fold(success = {}, failure = { getFuelRequest(it.response.headers["X-Transmission-Session-Id"]!![0]) }) }
} else {
    Log.d("__APP", sessionId)
    Fuel.post("http://213.251.183.154:12754/transmission/rpc")
            .header("Authorization" to "Basic")
            .header("X-Transmission-Session-Id" to sessionId)
            .body(getTorrentGetQuery())
}


fun getTorrents(sessionId: String) = IO.async<String> { callback ->
    getFuelRequest(sessionId).responseString { _, _, result ->
        result.fold(success = { callback(it.right()) }, failure = { callback(it.left()) }) }
}
