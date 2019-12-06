package com.tsquare.speakfriend.database.connection

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

class Conn {
    init {
        Database.connect("jdbc:sqlite:friend.db?foreign_keys=true", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}