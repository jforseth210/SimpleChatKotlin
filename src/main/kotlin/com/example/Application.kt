package com.example

import com.example.dao.DatabaseSingleton
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    DatabaseSingleton.init(environment)
    configureSerialization()
    configureSecurity()
    configureRouting()
}
