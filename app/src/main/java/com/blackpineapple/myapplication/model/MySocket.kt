package com.blackpineapple.myapplication.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket


class MySocket(private val callback: ServerCallback) {
    private var sock: Socket = Socket()
    private lateinit var listenerThread: Thread

    suspend fun connect(host: String, port: Int) {
        withContext(Dispatchers.IO) {
            runCatching {
                sock.connect(InetSocketAddress(host, port), 2500)
                if (sock.isConnected) {
                    sendStartMessage()

                    listenerThread = SocketListenThread(sock, callback)
                    listenerThread.start()
                }
            }.onFailure {
                Log.e("sock", "Error on connect: ", it)
            }
        }
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            runCatching {
                if (sock.isConnected) {
                    sendCloseMessage()
                    listenerThread.interrupt()
                    sock.close()
                }
            }.onFailure {
                Log.e("sock", "Error on disconnect: ", it)
            }
        }
    }

    suspend fun sendUpdateMessage(trashCapacity: Int, trashFilled: Int, trashStatus: Boolean) {
        withContext(Dispatchers.IO) {
            runCatching {
                if (sock.isConnected) {
                    val msg = Messages.getUpdateMessage(trashCapacity, trashFilled, trashStatus)
                    sock.getOutputStream().write(msg.encodeToByteArray())
                }
            }.onFailure {
                Log.e("sock", "Error on sendUpdateMessage:", it)
            }
        }
    }

    private suspend fun sendStartMessage() {
        withContext(Dispatchers.IO) {
            runCatching {
                if (sock.isConnected) {
                    val msg = Messages.getStartMessage()
                    sock.getOutputStream()!!.write(msg.encodeToByteArray())
                }
            }.onFailure {
                Log.e("sock", "Error on sendStartMessage: ", it)
            }
        }
    }

    private suspend fun sendCloseMessage() {
        withContext(Dispatchers.IO) {
            runCatching {
                if (sock.isConnected) {
                    val msg = Messages.getCloseMessage()
                    sock.getOutputStream()!!.write(msg.encodeToByteArray())
                }
            }.onFailure {
                Log.e("sock", "Error on sendCloseMessage: ", it)
            }
        }
    }

    fun isConnected() = sock.isConnected
}