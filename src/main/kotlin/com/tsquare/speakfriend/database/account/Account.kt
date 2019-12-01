package com.tsquare.speakfriend.database.account

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class Account {
    init {
        Database.connect("jdbc:sqlite:friend.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    fun create(userIdArg: Int, userArg: String?, passArg: String?, urlArg: String?, notesArg: String?) {
        transaction {
            AccountEntity.new {
                userId = userIdArg
                name = userArg
                pass = passArg
                url = urlArg
                notes = notesArg
            }
        }
    }
}
