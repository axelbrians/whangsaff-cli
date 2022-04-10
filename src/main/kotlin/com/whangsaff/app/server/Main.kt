package com.whangsaff.app.server

import com.whangsaff.app.common.Message
import com.whangsaff.app.common.User
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLServerSocketFactory

fun main() {

    val port = 443
    with(WhangsaffServer()) {
        serve(port)
    }
}