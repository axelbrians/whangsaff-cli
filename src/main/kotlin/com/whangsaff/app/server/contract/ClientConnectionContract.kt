package com.whangsaff.app.server.contract

import com.whangsaff.app.client.WhangsaffClient
import com.whangsaff.app.common.Message

interface ClientConnectionContract {

    fun onClientConnected(key: String, client: WhangsaffClient)

    fun onClientDisconnected(key: String, client: WhangsaffClient)

    fun onBroadcastMessage(senderKey: String, message: Message)

    fun onPrivateMessage(message: Message, client: WhangsaffClient)

    fun onShowOnline(client: WhangsaffClient, senderKey: String)
}