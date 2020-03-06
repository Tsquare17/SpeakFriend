package com.tsquare.speakfriend.database.tables

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Settings : IntIdTable()
{
    val userId = Settings.integer("user_id")
            .references(Users.id, ReferenceOption.CASCADE).index()
    val option = Settings.varchar("option", 255).uniqueIndex()
    val value  = Settings.varchar("value", 255)
}
