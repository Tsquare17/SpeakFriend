package com.tsquare.speakfriend.account

import com.tsquare.speakfriend.database.tables.Accounts
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class Account {
    fun create(user: String, pass: String, url: String) {
        Database.connect("jdbc:sqlite:friend.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            Accounts.insert {
                it[name]   = user
                it[Accounts.pass]   = pass
                it[Accounts.url]    = url
            }
        }
    }
}
