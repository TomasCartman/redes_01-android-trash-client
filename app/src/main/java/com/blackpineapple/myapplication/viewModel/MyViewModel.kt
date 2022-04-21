package com.blackpineapple.myapplication.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackpineapple.myapplication.model.MySocket
import com.blackpineapple.myapplication.model.ServerCallback
import com.blackpineapple.myapplication.model.ServerMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {
    private var mySocket: MySocket
    var isButtonEnabled: MutableState<Boolean> = mutableStateOf(true)
    var trashCapacity: MutableState<Int> = mutableStateOf(0)
    var trashFilled: MutableState<Int> = mutableStateOf(0)
    var isConnectionEstablished: MutableState<Boolean> = mutableStateOf(true)

    private val listener = object : ServerCallback {
        override fun onServerResponse(response: ServerMessage) {
            when(response.type) {
                "lock" -> lockTrash()
                "unlock" -> unlockTrash()
                "close" -> disconnect()
            }
        }
    }

    init {
        mySocket = MySocket(listener)
    }

    fun connect(host: String, port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            mySocket.connect(host, port)
        }.invokeOnCompletion {
            if (!mySocket.isConnected()) {
                isConnectionEstablished.value = false
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            mySocket.disconnect()
        }
    }

    private fun changeTrashState() {
        viewModelScope.launch(Dispatchers.IO) {
            mySocket.sendUpdateMessage(trashCapacity.value, trashFilled.value, !isButtonEnabled.value )
        }
    }

    fun putTrash() {
        if(trashCapacity.value >= trashFilled.value + 1) {
            trashFilled.value = trashFilled.value + 1
            if (trashCapacity.value == trashFilled.value) {
                isButtonEnabled.value = false
            }
            changeTrashState()
        }
    }

    fun emptyTrash() {
        trashFilled.value = 0
        changeTrashState()
    }

    fun changeCapacity(capacity: Int) {
        if(capacity > trashFilled.value) {
            trashCapacity.value = capacity
            changeTrashState()
        }
    }

    fun lockTrash() {
        isButtonEnabled.value = false
        changeTrashState()
    }

    private fun unlockTrash() {
        isButtonEnabled.value = true
        changeTrashState()
    }
}