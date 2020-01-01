package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.database.tables.Accounts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.tsquare.speakfriend.database.connection.Conn

class Account {
    init {
        Conn()
    }

    fun create(userIdArg: Int, nameArg: String?, passArg: String?, urlArg: String?, notesArg: String?) {
        transaction {
            AccountEntity.new {
                userId = userIdArg
                name = nameArg
                pass = passArg
                url = urlArg
                notes = notesArg
            }
        }
    }

    fun update(accountIdArg: Int, nameArg: String?, passArg: String?, urlArg: String?, notesArg: String?) {
        transaction {
            AccountEntity[accountIdArg].apply {
                name = nameArg
                pass = passArg
                url = urlArg
                notes = notesArg
            }
        }
    }

    fun delete(accountIdArg: Int) {
        transaction {
            AccountEntity[accountIdArg].delete()
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

    fun getById(id: Int): AccountEntity? {
        return transaction {
            AccountEntity.findById(id)
        }
    }
}
