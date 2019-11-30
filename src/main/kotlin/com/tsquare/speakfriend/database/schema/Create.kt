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

//            SchemaUtils.drop(Users, Accounts)
        }
    }
}
