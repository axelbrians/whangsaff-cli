package com.whangsaff.app.common

import java.io.Serializable

data class Online(
    val onlineUser : MutableList<String>?
) : Serializable
