package com.whangsaff.app.server

import com.whangsaff.app.common.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket

class WhangsaffServer(
    private val port: Int
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var serverJob: Job? = null

    fun serve() {
        val server = ServerSocket(port)
        println("= = = = Server up and Running at $port = = = =")

        while (true) {
            println("= = = Waiting for client to connect = = =")
            val client = server.accept()
            println("= = = Connected = = =")

            val objectInput = ObjectInputStream(client.getInputStream())
            val objectOutput = ObjectOutputStream(client.getOutputStream())

            val message = objectInput.readObject() as Message
            objectOutput.writeObject(message)
            objectOutput.flush()


            client.close()

        }
    }
}