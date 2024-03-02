package com.example.models

import User
import Users
import com.example.models.Message.Companion.referrersOn
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Conversations : IntIdTable() {
}
class Conversation(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<Conversation>(Conversations)
  val messages by Message referrersOn Messages.conversation
  var users by User via ConversationUsers
}
object ConversationUsers : Table() {
  val conversation = reference("conversation", Conversations)
  val user = reference("user", Users)
  override val primaryKey = PrimaryKey(conversation, user)
}