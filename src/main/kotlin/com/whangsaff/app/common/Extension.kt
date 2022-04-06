package com.whangsaff.app.common

import java.net.Socket

fun Socket.getSocketKey(): String {
    return this.remoteSocketAddress.toString()
}