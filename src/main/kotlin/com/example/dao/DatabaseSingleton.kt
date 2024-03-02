package com.example.dao

//import com.example.models.*
import Users
import com.example.models.ConversationUsers
import com.example.models.Conversations
import com.example.models.Messages
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import io.ktor.server.application.ApplicationEnvironment
object DatabaseSingleton {
  fun init(environment:ApplicationEnvironment) {
    val driverClassName = "org.mariadb.jdbc.Driver"
    val jdbcURL = environment.config.property("db.jdbcURL").getString()
    val username = environment.config.property("db.username").getString()
    val password = environment.config.property("db.password").getString()
    val database = Database.connect(jdbcURL, driverClassName, user=username, password =password)
    transaction(database) {
      SchemaUtils.create(Users)
      SchemaUtils.create(Messages)
      SchemaUtils.create(Conversations)
      SchemaUtils.create(ConversationUsers)
    }
  }

  suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
}