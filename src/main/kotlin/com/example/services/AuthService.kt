package com.example.services

import User
import Users
import com.example.dao.DatabaseSingleton.dbQuery
import org.mindrot.jbcrypt.BCrypt

/**
 * Business logic and database interactions for working with users
 * @author Justin Forseth
 */
suspend fun getUser(username: String): User? {
  var user: User? = null
  dbQuery {
    user = User.find { Users.username eq username }.firstOrNull()
  }
  return user
}

suspend fun checkLogin(username: String, password: String): Boolean {
  val user = getUser(username)
  if (user == null) return false
  return BCrypt.checkpw(password, user.hashedPassword)
}

suspend fun createUser(newUsername: String, password: String) {
  dbQuery {
    User.new {
      username = newUsername
      hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12))
    }
  }
}