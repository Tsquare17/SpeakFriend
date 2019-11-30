package com.tsquare.speakfriend.database.user

import com.tsquare.speakfriend.database.tables.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class User {
    fun create(user: String, pass: String) {
        Database.connect("jdbc:sqlite:friend.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val hashedPass = hashPass(pass);

        transaction {
            Users.insert {
                it[Users.name] = user
                it[Users.pass] = hashedPass
            }
        }
    }

    private fun hashPass(pass: String): String {
        return pass;
    }
}