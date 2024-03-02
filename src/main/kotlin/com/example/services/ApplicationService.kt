package com.example.services

import User
import com.example.dao.DatabaseSingleton.dbQuery
import com.example.dao.models.Conversation
import com.example.dao.models.Message
import org.jetbrains.exposed.sql.SizedCollection
import java.time.Instant

data class SerializableConversation(val user: String, val id: Int, val messages: List<SerializableMessage>)
data class SerializableMessage(val text: String, val to: String, val from: String)

/**
 * Message and conversation crud (or at least cr) functions
 * @author Justin Forseth
 */
suspend fun getSerializedConversations(user: User): List<SerializableConversation> {
  return user.conversations.map {
    SerializableConversation(
      user = it.users.filter { it.username != user.username }[0].username,
      id = it.id.value,
      messages = it.messages.map {
        SerializableMessage(
          from = it.fromUser.username,
          to = it.toUser.username,
          text = it.text
        )
      }
    )
  }
}

suspend fun sendMessage(currentConversation: Conversation, currentUser: User, message: String) {
  dbQuery {
    val otherUser = currentConversation.users.filter { it.username != currentUser.username }[0]
    Message.new {
      text = message
      conversation = currentConversation
      timestamp = Instant.now()
      fromUser = currentUser
      toUser = otherUser
    }
  }
}

suspend fun createConversation(conversationUsers: List<User>): Conversation {
  return dbQuery {
    return@dbQuery Conversation.new {
      users = SizedCollection(conversationUsers)
    }
  }
}