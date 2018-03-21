package com.example.remimichel.seedboxdownloader.data.remote

import arrow.core.Either
import arrow.effects.IO
import arrow.syntax.either.left
import arrow.syntax.either.right
import com.example.remimichel.seedboxdownloader.data.Torrent
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import org.json.JSONObject

val baseEndpoint = "http://213.251.183.154:12754/transmission/"
val credentials = "Basic bWljaGVscmUzODppeGU3eWllbW0zOA=="

sealed class HTTPException {
    object Confilct409 : HTTPException()
}

fun getTorrentGetQuery() = jsonObject(
        "method" to "torrent-get",
        "arguments" to jsonObject("fields" to jsonArray("id", "name"))).toString()

fun getFuelRequest(sessionId: String, query: String): Request = Fuel.post(baseEndpoint + "rpc")
        .header("Authorization" to credentials)
        .header("X-Transmission-Session-Id" to sessionId)
        .body(query)

fun <T> makeResponse(response: Response, body: Result<String, FuelError>, cb: (String) -> T): Either<FuelError, T> =
        if(response.statusCode == 200) Either.right(cb(body.component1()!!)) else Either.left(body.component2()!!)

fun getXTransmissionSessionId() = IO.async<String> {
    Fuel.get(baseEndpoint)
            .header("Authorization" to credentials)
            .responseString { _, res, response -> it(makeResponse(res, response, { it })) }
}

fun responseToTorrents(res: String): List<Torrent> = Gson().fromJson((JSONObject(res)["arguments"] as JSONObject)
        .getJSONArray("torrents").toString())


fun getTorrents(sessionId: String) = IO.async<List<Torrent>> {
    getFuelRequest(sessionId, getTorrentGetQuery())
            .responseString { _, res, body -> it(makeResponse(res, body, ::responseToTorrents)) }
}
