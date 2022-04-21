package com.blackpineapple.myapplication.model

import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.Socket

interface ServerCallback {
    fun onServerResponse(response: ServerMessage)
}

class SocketListenThread(private val sock: Socket, private val callback: ServerCallback) : Thread() {
    override fun run() {
        super.run()

        try {
            while(sock.isConnected) {
                val inStream = sock.getInputStream()
                if (inStream.available() > 0) {
                    val response = ByteArray(4096)
                    inStream.read(response)
                    val count = response.count { it > 0 }
                    val stringResponse = response.decodeToString(0, count)
                    val serverResponse = Json.decodeFromString<ServerMessage>(stringResponse)

                    callback.onServerResponse(serverResponse)
                }
                sleep(500)
            }
        } catch (e: InterruptedException) {
            Log.w("SocketListenThread", "InterruptedException", e)
        }
    }
}