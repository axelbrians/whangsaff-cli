package com.whangsaff.app.client

import com.whangsaff.app.common.Message
import com.whangsaff.app.common.getSocketKey
import com.whangsaff.app.server.contract.ClientConnectionContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.SocketException

class WhangsaffClient(
    private val socket: Socket,
    private val clientContract: ClientConnectionContract
) {

    private val socketKey = socket.getSocketKey()
    private val inputStream = ObjectInputStream(socket.getInputStream())
    private val outputStream = ObjectOutputStream(socket.getOutputStream())

    var isConnected = false
        private set

    fun serve() {
        try {
            clientContract.onClientConnected(socketKey, this)
            while (true) {
                val message = inputStream.readObject() as Message
                clientContract.onBroadcastMessage(socketKey, message)
            }
        } catch (e: SocketException) {
            disconnect()
        }


    }

    fun sendMessage(message: Message) {
        with(outputStream) {
            writeObject(message)
            flush()
        }
    }

    fun connect() {
        isConnected = true
    }

    fun disconnect() {
        clientContract.onClientDisconnected(socketKey, this)
        isConnected = false
        socket.close()
    }
}