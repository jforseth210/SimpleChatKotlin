package com.example.dao.models

import User
import Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

/**
 * Database objects for representing conversations
 * @author Justin Forseth
 */

object Conversations : IntIdTable() {
}

class Conversation(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<Conversation>(Conversations)

  val messages by Message referrersOn Messages.conversation
  var users by User via ConversationUsers
}

// Joiner table
object ConversationUsers : Table() {
  val conversation = reference("conversation", Conversations)
  val user = reference("user", Users)
  override val primaryKey = PrimaryKey(conversation, user)
}