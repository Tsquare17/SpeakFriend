package com.tsquare.speakfriend.database.user

import com.tsquare.speakfriend.database.connection.Conn
import com.tsquare.speakfriend.database.tables.Users
import org.jetbrains.exposed.sql.transactions.transaction

class User {
    init {
        Conn()
    }

    fun create(user: String, password: String): Boolean {
        try {
            transaction {
                UserEntity.new {
                    name = user
                    pass = password
                }
            }
        } catch (e: Exception) {
            return false;
        }
        return true;
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

    fun delete(id: Int) {
        transaction {
            UserEntity.findById(id)?.delete()
        }
    }
}
