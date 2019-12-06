package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.database.tables.Accounts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.tsquare.speakfriend.database.connection.Conn

class Account {
    init {
        Conn()
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
