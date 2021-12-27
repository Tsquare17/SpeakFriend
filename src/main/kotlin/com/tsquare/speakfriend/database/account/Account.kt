package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.auth.CurrentUser
import com.tsquare.speakfriend.database.tables.Accounts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.tsquare.speakfriend.database.connection.Conn
import com.tsquare.speakfriend.state.State

class Account {
    init {
        Conn()
    }

    fun create(userIdArg: Int, nameArg: String?, userArg: String?, passArg: String?, urlArg: String?, notesArg: String?): AccountEntity {
        State.isDirtyAccounts = 1
        return transaction {
            AccountEntity.new {
                userId = userIdArg
                name = nameArg
                user = userArg
                pass = passArg
                url = urlArg
                notes = notesArg
            }
        }
    }

    fun update(accountIdArg: Int, nameArg: String?, userArg: String?, passArg: String?, urlArg: String?, notesArg: String?) {
        State.isDirtyAccounts = 1
        transaction {
            AccountEntity[accountIdArg].apply {
                name = nameArg
                user = userArg
                pass = passArg
                url = urlArg
                notes = notesArg
            }
        }
    }

    fun delete(accountIdArg: Int) {
        State.isDirtyAccounts = 1
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

    fun getByIds(ids: List<Int>): List<AccountEntity> {
        return transaction {
            AccountEntity.find {
                Accounts.id inList ids
            }.orderBy(
                Accounts.name.lowerCase() to SortOrder.ASC
            ).toList()
        }
    }

    fun getByName(name: String): AccountEntity? {
        return transaction {
            AccountEntity.find {
                Accounts.userId eq CurrentUser.userId
                Accounts.name eq name
            }.firstOrNull()
        }
    }
}
