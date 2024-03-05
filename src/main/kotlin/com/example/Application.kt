package com.example

import com.example.dao.DatabaseSingleton
import com.example.plugins.configureSerialization
import com.example.routes.configureApplicationRoutes
import com.example.routes.configureAuthRoutes
import io.ktor.server.application.*

/**
 * @author Justin Forseth
 * Entry point to start up the server
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
  DatabaseSingleton.init(environment)
  configureSerialization()
  configureSecurity()
  configureAuthRoutes()
  configureApplicationRoutes()
}
