package com.whangsaff.app.client

import com.whangsaff.app.common.Message
import com.whangsaff.app.common.Online
import com.whangsaff.app.common.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

fun main() {
    val socket = Socket("localhost", 443)

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var writeJob: Job? = null
    var readJob: Job? = null


    println("= = = Connected to localhost:443 = = =")
    val objectOutput = ObjectOutputStream(socket.getOutputStream())


    readJob = coroutineScope.launch {
        val objectInput = ObjectInputStream(socket.getInputStream())

        while (true) {
            val response = objectInput.readObject() as Message
            if (response.type == 2 && response.sender == "Server") {
                val listOnline: Online = response.text as Online
                println("Online:")
                for (online in listOnline.onlineUser!!) {
                    println(online)
                }
            }
            else {
                println("= = = New Message = = =")
                println("sender: ${response.sender}")
                println(response.text)
            }
            println("= = = = = =")

        }
    }


    println("Enter your username (1 word only):")
    val name = readln()
    val user = User(name)
    with(objectOutput) {
        writeObject(user)
        flush()
    }

    while (true) {
        println("= = = Whangsaff Client = = =")
        println("Type \"\\b Your Message Here\" to send Broadcast message")
        println("Type \"\\s\" to show online user")
        println("Type \"\\w Destination \\m Your Message Here\" to send private message")

        val text = readln()
        var message = Message(0, "", "", "")
        if (text.startsWith("\\b")) {
            val msg = text.removePrefix("\\b ")
            message = Message(1, msg, name, "")
        }
        else if (text.startsWith("\\s")) {
            message = Message(2, "", name, "")
        }
        else if (text.startsWith("\\w")) {
            var receiver = text.substringAfter("\\w ")
            receiver = receiver.substringBefore(" \\m", "No Target")
            if(receiver == "No Target") {
                println("Error! No Receiver")
                continue
            }
            val msg = text.substringAfter("\\m ")
            message = Message(3, msg, name, receiver)
        }
        else if (text.equals(".exit", true)) {
            readJob.cancel()
            break
        }
        else {
            println("Unrecognized command")
            continue
        }

//        val message = Message(text, name)

        with(objectOutput) {
            writeObject(message)
            flush()
        }
    }
}