package com.whangsaff.app.client

import com.whangsaff.app.common.Message
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

fun main() {
    val socket = Socket("localhost", 443)

    println("= = = Connected to localhost:443 = = =")

    while (true) {

        val objectInput = ObjectInputStream(socket.getInputStream())
        val objectOutput = ObjectOutputStream(socket.getOutputStream())

        println("Enter your message (1 line only):")

        val text = readln()
        if (text.equals("exit", true)) {
            break
        }
        println("Enter your name (1 line only):")
        val name = readln()

        val message = Message(text, name)



        objectOutput.writeObject(message)
        objectOutput.flush()

        val response = objectInput.readObject() as Message
        println("Response from server: $response")

    }

    socket.close()
}