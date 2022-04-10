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
import kotlin.system.exitProcess

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
        var crashCounter = 0
        val objectInput = ObjectInputStream(inputStream)

        while (true) {
            if (crashCounter > 3) {
                println("= = = Connection Lost = = =")
                exitProcess(0)
            }

            try {
                val response = objectInput.readObject() as Message
                if (response.type == MessageType.ONLINE.value && response.sender == "Server") {
                    val onlineUsers = response.text as Online
                    println("Online users:")
                    for (online in onlineUsers.onlineUser) {
                        println(online)
                    }
                    crashCounter = 0
                } else {
                    println("= = = New Message, from: ${response.sender} = = =")
                    println(response.text)
                    crashCounter = 0
                }
                println("= = = = = =")
            } catch (e: Exception) {
                crashCounter++
                println("= = = Something went wrong, message not received = = =")
                delay(1000)
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