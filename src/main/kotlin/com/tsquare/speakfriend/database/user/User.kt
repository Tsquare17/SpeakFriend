package com.tsquare.speakfriend.database.user

import com.tsquare.speakfriend.crypt.Crypt
import com.tsquare.speakfriend.database.tables.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class User {
    init {
        Database.connect("jdbc:sqlite:friend.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    fun create(user: String, password: String) {
        val hashedPass = Crypt.hash(password);
        transaction {
            UserEntity.new {
                name = user
                pass = hashedPass
            }
        }
    }

    fun get(id: Int) {
        return transaction {
            UserEntity.findById(id)
        }
    }

    fun get(name: String): UserEntity? {
        return transaction {
            UserEntity.find {
                Users.name eq name
            }.firstOrNull()
        }
    }
}