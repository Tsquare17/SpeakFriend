package com.tsquare.speakfriend.database.connection

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.io.File
import java.sql.Connection

class Conn() {
    init {
        val dir = File(System.getProperty("user.home").toString() + "/.speakfriend")
        if(!dir.exists()) {
            dir.mkdir()
        }
        val db = System.getProperty("user.home") + "/.speakfriend/friend.db"
        Database.connect("jdbc:sqlite:$db?foreign_keys=true", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}
