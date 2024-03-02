package com.example.models

import User
import Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Messages : IntIdTable() {
  val text = text("text")
  val timestamp = timestamp("timestamp")
  val conversation = reference("conversation", Conversations)
  val toUser = reference("to_user", Users)
  val fromUser = reference("from_user", Users)
}
class Message(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<Message>(Messages)
  var text by Messages.text
  var timestamp by Messages.timestamp
  var conversation by Conversation referencedOn Messages.conversation
  var toUser by User referencedOn Messages.toUser
  var fromUser by User referencedOn Messages.fromUser
}