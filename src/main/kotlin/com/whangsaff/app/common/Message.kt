package com.whangsaff.app.common

import java.io.Serializable

data class Message(
    val type: Int,
    val text: Any,
    val sender: String,
    val receiver: String
): Serializable
