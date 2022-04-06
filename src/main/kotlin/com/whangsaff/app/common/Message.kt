package com.whangsaff.app.common

import java.io.Serializable

data class Message(
    val text: String,
    val sender: String
): Serializable
