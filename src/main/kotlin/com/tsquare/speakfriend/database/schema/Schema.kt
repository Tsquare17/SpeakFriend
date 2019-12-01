package com.tsquare.speakfriend.database.schema

import com.tsquare.speakfriend.database.tables.Users
import com.tsquare.speakfriend.database.tables.Accounts
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class Schema {
    init {
        Database.connect("jdbc:sqlite:friend.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    fun up() {
        transaction {
            SchemaUtils.create(Users, Accounts)
        }
    }
    fun down() {
        transaction {
            SchemaUtils.drop(Users, Accounts)
        }
    }
}
