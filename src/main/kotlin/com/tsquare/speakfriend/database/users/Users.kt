package com.tsquare.speakfriend.database.users

import org.jetbrains.exposed.sql.Table

object Users : Table()
{
    val id   = Users.integer("id").autoIncrement().primaryKey()
    val name = Users.varchar("name", 50)
    val pass = Users.varchar("pass", 255)
}
