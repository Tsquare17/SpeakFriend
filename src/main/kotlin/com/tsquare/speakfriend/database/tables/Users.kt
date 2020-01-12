package com.tsquare.speakfriend.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object Users : IntIdTable()
{
    val name = Users.varchar("name", 255).uniqueIndex()
    val pass = Users.varchar("pass", 255)
}
