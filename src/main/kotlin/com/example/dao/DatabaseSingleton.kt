package com.example.dao

//import com.example.dao.models.*
import Users
import com.example.dao.models.ConversationUsers
import com.example.dao.models.Conversations
import com.example.dao.models.Messages
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Set up database connection
 * @author Justin Forseth
 */
object DatabaseSingleton {
  fun init(environment: ApplicationEnvironment) {
    val driverClassName = "org.mariadb.jdbc.Driver"
    val jdbcURL = environment.config.property("db.jdbcURL").getString()
    val username = environment.config.property("db.username").getString()
    val password = environment.config.property("db.password").getString()
    val database = Database.connect(jdbcURL, driverClassName, user = username, password = password)
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