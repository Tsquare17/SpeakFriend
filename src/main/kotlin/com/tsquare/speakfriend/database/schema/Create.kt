package com.tsquare.speakfriend.database.schema

import com.tsquare.speakfriend.database.tables.Users
import com.tsquare.speakfriend.database.tables.Accounts
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class Create
{
    init {
        Database.connect("jdbc:sqlite:friend.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            SchemaUtils.create(Users, Accounts)

//            Users.insert {
//                it[id]   = 1
//                it[name] = "Trevor"
//                it[pass] = "password"
//            }
//
//            Accounts.insert {
//                it[id]     = 1
//                it[userId] = 1
//                it[name]   = "name"
//                it[pass]   = "pass"
//                it[url]    = "example.com"
//            }
//
//            SchemaUtils.drop(Users, Accounts)
        }
    }
}
