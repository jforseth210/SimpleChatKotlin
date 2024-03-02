package com.example.plugins

import User
import com.example.SuccessResponse
import com.example.UserSession
import com.example.dao.DatabaseSingleton.dbQuery
import com.example.models.Conversation
import com.example.models.Message
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.SizedCollection
import java.io.File
import java.time.Instant

data class SerializableConversation(val user: String, val id: Int, val messages: List<SerializableMessage>)
data class SerializableMessage(val text: String, val to: String, val from:String)
data class MessageForm(val message: String,val  conversationId: Int)
data class ConversationForm(val  user: String)
data class ConversationCreationResponse(val success:Boolean,val id: Int)
fun Application.configureRouting() {
    routing {
        // Static plugin. Try to access `/static/index.html`
        staticFiles("/", File("web/dist")) {
        }
        authenticate("auth-session") {
            get("/conversations") {
                dbQuery {
                    val userSession = call.sessions.get<UserSession>()
                    val username: String = userSession?.username ?: run {
                        call.respond(listOf<Conversation>())
                        return@dbQuery
                    }
                    var user = User.find { Users.username eq username }.firstOrNull()

                    if (user == null) return@dbQuery
                    call.respond(user.conversations.map {
                        SerializableConversation(
                            user = it.users.filter { it.username != username }[0].username,
                            id = it.id.value,
                            messages = it.messages.map {
                                SerializableMessage(
                                    from = it.fromUser.username,
                                    to = it.toUser.username,
                                    text = it.text
                                )
                            }
                        )
                    })
                }
            }
        }
            authenticate("auth-session") {
                post("/send-message") {
                    dbQuery {
                        val userSession = call.sessions.get<UserSession>()
                        val username: String = userSession?.username ?: run {
                            call.respond(listOf<Conversation>())
                            return@dbQuery
                        }
                        val currentUser = User.find { Users.username eq username }.firstOrNull()
                        if (currentUser == null) {
                            call.respond(404)
                            return@dbQuery
                        }

                        val messageForm = call.receive<MessageForm>()
                        val currentConversation = Conversation.findById(messageForm.conversationId)
                        if (currentConversation == null) {
                            call.respond(404)
                            return@dbQuery
                        }

                        val otherUser = currentConversation.users.filter { it.username != currentUser.username }[0]
                        Message.new {
                            text = messageForm.message
                            conversation = currentConversation
                            timestamp = Instant.now()
                            fromUser = currentUser
                            toUser = otherUser
                        }
                    }
                    call.respond(SuccessResponse(true))
                }
            }
            authenticate("auth-session") {
                post("/add-conversation") {
                    dbQuery {
                        val conversationForm = call.receive<ConversationForm>()
                        val otherUsername = conversationForm.user
                        val userSession = call.sessions.get<UserSession>()
                        println(userSession)
                        val username: String = userSession?.username ?: run {
                            call.respond(listOf<Conversation>())
                            return@dbQuery
                        }
                        val currentUser = User.find { Users.username eq username }.firstOrNull()
                        if (currentUser == null) {
                            call.respond(404)
                            return@dbQuery
                        }
                        val otherUser = User.find { Users.username eq otherUsername }.firstOrNull()
                        if (otherUser == null) {
                            call.respond(404)
                            return@dbQuery
                        }
                        if (currentUser == otherUser) {
                            call.respond(400)
                            return@dbQuery
                        }
                        val conversation = Conversation.new {
                            users = SizedCollection(listOf(currentUser, otherUser))
                        }
                        call.respond(ConversationCreationResponse(true, conversation.id.value))

                    }
                }
            }
        }

}

