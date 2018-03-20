package com.example.remimichel.seedboxdownloader.data.remote

import arrow.effects.IO
import arrow.syntax.either.right
import com.example.remimichel.seedboxdownloader.data.Torrent
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import org.json.JSONObject

val baseEndpoint = "http://213.251.183.154:12754/transmission/"

sealed class HTTPException {
    object Confilct409 : HTTPException()
}

fun getTorrentGetQuery() = jsonObject(
        "method" to "torrent-get",
        "arguments" to jsonObject("fields" to jsonArray("id", "name"))).toString()

fun getFuelRequest(sessionId: String, query: String): Request = Fuel.post(baseEndpoint + "rpc")
        .header("Authorization" to "Basic")
        .header("X-Transmission-Session-Id" to sessionId)
        .body(query)

fun getXTransmissionSessionId() = IO.async<String> {
    Fuel.get(baseEndpoint)
            .header("Authorization" to "Basic")
            .responseString { _, res, _ -> it(res.headers["X-Transmission-Session-Id"]!![0].right()) }
}

fun responseToTorrents(res: String): List<Torrent> = Gson().fromJson((JSONObject(res)["arguments"] as JSONObject)
        .getJSONArray("torrents").toString())


fun getTorrents(sessionId: String) = IO.async<List<Torrent>> {
    getFuelRequest(sessionId, getTorrentGetQuery())
            .responseString { _, _, res -> it(responseToTorrents(res.component1()!!).right()) }
}
