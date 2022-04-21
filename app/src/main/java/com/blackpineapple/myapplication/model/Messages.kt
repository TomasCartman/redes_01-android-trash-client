package com.blackpineapple.myapplication.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class StartMessage(val type: String, val sender: String, val mac: String)

@Serializable
data class UpdateMessage(val type: String, val sender: String, val trash_capacity: Int,
                                 val trash_filled: Int, val trash_status: Boolean, val mac: String)

@Serializable
data class ServerMessage(val type: String, val sender: String)

class Messages {
    companion object {
        fun getStartMessage(): String {
            val data = StartMessage("start", "trash", "02:00:00:00:00")
            return Json.encodeToString(data)
        }

        fun getUpdateMessage(trashCapacity: Int, trashFilled: Int, trashStatus: Boolean): String {
            val data = UpdateMessage("update", "trash", trashCapacity, trashFilled,
                trashStatus, "02:00:00:00:00")
            return Json.encodeToString(data)
        }

        fun getCloseMessage(): String {
            val data = StartMessage("close","trash", "02:00:00:00:00")
            return Json.encodeToString(data)
        }
    }
}