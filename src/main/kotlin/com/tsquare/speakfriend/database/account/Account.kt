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

    fun create(userIdArg: Int, nameArg: String?, userArg: String?, passArg: String?, urlArg: String?, notesArg: String?) {
        State.isDirtyAccounts = 1
        transaction {
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

    fun create(userIdArg: Int, nameArg: String?, userArg: String?, passArg: String?, urlArg: String?, notesArg: String?, cloudIdArg: Int) {
        State.isDirtyAccounts = 1
        transaction {
            AccountEntity.new {
                userId = userIdArg
                name = nameArg
                user = userArg
                pass = passArg
                url = urlArg
                notes = notesArg
                cloudId = cloudIdArg
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

    fun update(accountIdArg: Int, nameArg: String?, userArg: String?, passArg: String?, urlArg: String?, notesArg: String?, cloudIdArg: Int) {
        State.isDirtyAccounts = 1
        transaction {
            AccountEntity[accountIdArg].apply {
                name = nameArg
                user = userArg
                pass = passArg
                url = urlArg
                notes = notesArg
                cloudId = cloudIdArg
            }
        }
    }

    fun update(accountIdArg: Int, cloudIdArg: Int) {
        State.isDirtyAccounts = 1
        transaction {
            AccountEntity[accountIdArg].apply {
                cloudId = cloudIdArg
            }
        }
    }

    fun importOrUpdate(cloudIdArg: Int, nameArg: String?, userArg: String?, passArg: String?, urlArg: String?, notesArg: String?) {
        val existing = getByCloudId(cloudIdArg);
        if (existing != null) {
            update(existing.id.value, nameArg, userArg, passArg, urlArg, notesArg);
        } else {
            create(CurrentUser.userId, nameArg, userArg, passArg, urlArg, notesArg);
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

    fun getByCloudId(id: Int): AccountEntity? {
        return transaction {
            AccountEntity.find {
                Accounts.cloudId eq id
            }.orderBy(
                    Accounts.name.lowerCase() to SortOrder.ASC
            ).toList().firstOrNull()
        }
    }
}
