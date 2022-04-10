package com.whangsaff.app.server

import com.whangsaff.app.client.WhangsaffClient
import com.whangsaff.app.common.Message
import com.whangsaff.app.common.MessageType
import com.whangsaff.app.common.Online
import com.whangsaff.app.common.getSocketKey
import com.whangsaff.app.common.socket.SSLServerSocketKeystoreFactory
import com.whangsaff.app.server.contract.ClientConnectionContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.net.ServerSocket
import java.net.Socket
import javax.net.ssl.SSLServerSocket

class WhangsaffServer(): ClientConnectionContract {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val clientJobs = hashMapOf<String, Job>()
    private val connectedClients = hashMapOf<String, WhangsaffClient>()


    fun serve(port: Int) {
        println("${System.getProperty("user.dir")}\\Whangsaff_Tech_Ltd_private.jks")
        val server = initiateSSLSocket(port)
        println("= = = = Server up and Running at $port = = = =")

        while (true) {
            println("= = = Waiting for client to connect = = =")
            val socket = server.accept()
            println("= = = ${socket.getSocketKey()} Connected = = =")

            val job = coroutineScope.launch {
                val client = WhangsaffClient(socket, this@WhangsaffServer)
                client.serve()
            }

            handleClientJob(socket, job)
        }
    }


    private fun handleClientJob(socket: Socket, job: Job) {
        val prevJobs = clientJobs[socket.getSocketKey()]
        if (prevJobs != null) {
            job.cancel()
            with(BufferedOutputStream(socket.getOutputStream())) {
                write("Multiple connection within same IP is not allowed".toByteArray())
                flush()
            }
            socket.close()
        } else {
            clientJobs[socket.getSocketKey()] = job
        }
    }

    override fun onBroadcastMessage(senderKey: String, message: Message) {
        connectedClients.forEach { (_, client) ->
            client.sendMessage(message)
        }
    }

    override fun onPrivateMessage(message: Message, client: WhangsaffClient) {
        var receiverClient: WhangsaffClient? = null
        for ((_, wClient) in connectedClients) {
            if (wClient.username == message.receiver) {
                receiverClient = wClient
            }
        }

        if (receiverClient != null) {
            receiverClient.sendMessage(message)
        } else {
            val userNotOnlineMessage = message.copy(text = "${message.receiver} is not online")
            client.sendMessage(userNotOnlineMessage)
        }
    }

    override fun onShowOnline(client: WhangsaffClient, senderKey: String) {
        val userOnline: MutableList<String> = mutableListOf()
        connectedClients.forEach { (key, client) ->
            if (key != senderKey) {
                userOnline.add(client.username)
            }
        }
        val onlineUser = Online(userOnline)
        val message = Message(
            MessageType.ONLINE.value,
            onlineUser,
            "Server",
            client.username
        )
        client.sendMessage(message)
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

    private fun initiateSSLSocket(port: Int): ServerSocket {
        return SSLServerSocketKeystoreFactory
            .getServerSocketWithCert(
                port,
                "${System.getProperty("user.dir")}\\Whangsaff_Tech_Ltd_private.jks",
                "password123",
                SSLServerSocketKeystoreFactory.ServerSecureType.SSL
            )
    }

    private fun logException(e: Exception) {
        println("= = = Exception = = =")
        println(e.toString())
        println("= = = = = = =")
    }

}