import com.example.dao.models.Conversation
import com.example.dao.models.ConversationUsers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Database objects for representing users
 */
object Users : IntIdTable() {
  val username = varchar("username", 255)
  val hashedPassword = varchar("hashed_password", 255)
}

class User(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<User>(Users)

  var username by Users.username
  var hashedPassword by Users.hashedPassword
  var conversations by Conversation via ConversationUsers
}

