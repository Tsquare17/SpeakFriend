package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.database.tables.Accounts
import com.tsquare.speakfriend.database.tables.Users
import com.tsquare.speakfriend.database.user.UserEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    fun getByUserId(id: Int): List<AccountEntity> {
        return transaction {
            AccountEntity.find {
                Accounts.userId eq id
            }.orderBy(
                    Accounts.name.lowerCase() to SortOrder.ASC
            ).toList()
        }
    }

}
