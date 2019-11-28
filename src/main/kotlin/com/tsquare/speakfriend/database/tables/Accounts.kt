package com.tsquare.speakfriend.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption

object Accounts : Table()
{
    val id     = Accounts.integer("id").autoIncrement().primaryKey()
    val userId = Accounts.integer("user_id")
            .references(Users.id, ReferenceOption.CASCADE).index()
    val name   = Accounts.varchar("name", 50)
    val pass   = Accounts.varchar("pass", 255).nullable()
    val url    = Accounts.varchar("url", 255).nullable()
}
