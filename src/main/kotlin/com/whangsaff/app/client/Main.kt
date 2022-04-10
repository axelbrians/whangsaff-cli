package com.whangsaff.app.client

import com.whangsaff.app.common.Message
import com.whangsaff.app.common.MessageType
import com.whangsaff.app.common.Online
import com.whangsaff.app.common.User
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

fun main() {
    val socket = Socket("localhost", 443)
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    println("= = = Connected to localhost:443 = = =")

    val readJob = handleReadMessage(coroutineScope, socket.getInputStream())
    val objectOutput = ObjectOutputStream(socket.getOutputStream())


    println("Enter your username (1 line only):")
    val name = readln()
    val user = User(name)
    with(objectOutput) {
        writeObject(user)
        flush()
    }


    println("= = = Whangsaff-CLI = = =")
    printHelp()

    while (true) {
        println("= = = Whangsaff-CLI = = =")


        val text = readln()
        var message: Message
        if (text.startsWith("/b")) {
            val msg = text.removePrefix("/b ")
            message = Message(MessageType.BROADCAST.value, msg, name, "")
        } else if (text.startsWith("/s")) {
            message = Message(MessageType.ONLINE.value, "", name, "")
        } else if (text.equals("/exit", true)) {
            readJob.cancel()
            break
        } else if (text.startsWith("/w")) {
            var receiver = text.substringAfter("/w ")
            receiver = receiver.substringBefore(" /m", "No Target")
            if (receiver == "No Target") {
                println("Error! No Receiver")
                continue
            }
            val msg = text.substringAfter("/m ")
            message = Message(MessageType.WHISPER.value, msg, name, receiver)
        } else if (text.equals("/h", true)) {
            printHelp()
            continue
        } else {
            println("Unrecognized command")
            continue
        }


        with(objectOutput) {
            writeObject(message)
            flush()
        }
    }

    coroutineScope.cancel()
}

private fun handleReadMessage(coroutineScope: CoroutineScope, inputStream: InputStream): Job {
    return coroutineScope.launch {
        val objectInput = ObjectInputStream(inputStream)

        while (true) {
            try {
                val response = objectInput.readObject() as Message
                if (response.type == MessageType.ONLINE.value && response.sender == "Server") {
                    val listOnline: Online = response.text as Online
                    println("Online:")
                    for (online in listOnline.onlineUser!!) {
                        println(online)
                    }
                } else {
                    println("= = = New Message, from: ${response.sender} = = =")
                    println(response.text)
                }
                println("= = = = = =")
            } catch (e: Exception) {
                println("= = = Something went wrong, message not received = = =")
            }
        }
    }
}

private fun printHelp() {
    println("= = = Whangsaff-CLI Guide = = =")
    println("/b <message> - broadcast message")
    println("/s - see online users")
    println("/w <username> /m <message> - send message to user")
    println("/h - print help")
    println("/exit - exit")
    println("= = = = = =")
}