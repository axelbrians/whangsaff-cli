package com.whangsaff.app.client

import com.whangsaff.app.common.Message
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
            println("= = = New Message = = =")
            println("sender: ${response.sender}")
            println(response.text)
            println("= = = = = =")
        }
    }



    while (true) {
        println("Enter your message (1 line only):")
        val text = readln()
        if (text.equals(".exit", true)) {
            readJob.cancel()
            break
        }
        println("Enter your name (1 line only):")
        val name = readln()
        val message = Message(text, name)

        with(objectOutput) {
            writeObject(message)
            flush()
        }
    }
}