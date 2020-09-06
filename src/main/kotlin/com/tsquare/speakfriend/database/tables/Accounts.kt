package com.tsquare.speakfriend.database.tables

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Accounts : IntIdTable()
{
    val userId = Accounts.integer("user_id")
            .references(Users.id, ReferenceOption.CASCADE).index()
    val name   = Accounts.varchar("name", 255).nullable()
    val user   = Accounts.varchar("user", 255).nullable()
    val pass   = Accounts.varchar("pass", 255).nullable()
    val url    = Accounts.varchar("url", 255).nullable()
    val notes  = Accounts.text("notes").nullable()
    val cloudId = Accounts.integer("cloud_id").nullable()
}
