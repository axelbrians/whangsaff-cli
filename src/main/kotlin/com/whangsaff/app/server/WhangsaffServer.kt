package com.whangsaff.app.server

import com.whangsaff.app.client.WhangsaffClient
import com.whangsaff.app.common.Message
import com.whangsaff.app.common.Online
import com.whangsaff.app.server.contract.ClientConnectionContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.net.ServerSocket
import java.net.Socket

class WhangsaffServer(
    private val port: Int
): ClientConnectionContract {

    private val serverScope = CoroutineScope(Dispatchers.IO)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var serverJob: Job? = null
    private val clientJobs = hashMapOf<String, Job>()
    private val connectedClients = hashMapOf<String, WhangsaffClient>()


    fun serve() {
        val server = ServerSocket(port)
        println("= = = = Server up and Running at $port = = = =")

        while (true) {
            println("= = = Waiting for client to connect = = =")
            val socket = server.accept()
            println("= = = ${socket.remoteSocketAddress} Connected = = =")

            val job = coroutineScope.launch {
                val client = WhangsaffClient(socket, this@WhangsaffServer)
//                try {
                    client.serve()
//                } catch (e: Exception) {
//                    client.disconnect()
//                    logException(e)
//                }
            }

            handleClientJob(socket, job)
        }
    }


    private fun handleClientJob(socket: Socket, job: Job) {
        with(clientJobs[socket.remoteSocketAddress.toString()]) {
            if (this != null) {
                job.cancel()
                with(BufferedOutputStream(socket.getOutputStream())) {
                    write("Multiple connection within same IP is not allowed".toByteArray())
                    flush()
                }
                socket.close()
            } else {
                clientJobs[socket.remoteSocketAddress.toString()] = job
            }
        }
    }

    override fun onBroadcastMessage(senderKey: String, message: Message) {
        println("= = = Broadcasted Client = = =")
        println("${connectedClients.map { it.key }}")
        connectedClients.forEach { (key, client) ->
            if (key != senderKey) {
                client.sendMessage(message)
            }
        }
    }

    override fun onPrivateMessage(message: Message) {
        connectedClients.forEach { _, client ->
            if(client.username == message.receiver) {
                client.sendMessage(message)
            }
        }
    }

    override fun onShowOnline(senderKey: String) {
        var userOnline: MutableList<String> = mutableListOf()
        connectedClients.forEach { (key, client) ->
            if (key != senderKey) {
                userOnline.add(client.username)
            }
        }
        val onlineUser = Online(userOnline)
        val message = Message(2, onlineUser, "Server", "")
        connectedClients[senderKey]?.sendMessage(message)
    }

    override fun onClientConnected(key: String, client: WhangsaffClient) {
        client.connect()
        connectedClients[key] = client
    }

    override fun onClientDisconnected(key: String, client: WhangsaffClient) {
        try {
            println("= = = Client $key disconnected = = =")
            clientJobs[key]?.cancel()
            clientJobs.remove(key)
            connectedClients.remove(key)
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun logException(e: Exception) {

        println("= = = Exception = = =")
        println(e.toString())
        println("= = = = = = =")
    }

}