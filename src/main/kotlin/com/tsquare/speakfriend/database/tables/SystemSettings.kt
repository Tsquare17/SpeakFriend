package com.tsquare.speakfriend.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object SystemSettings : IntIdTable()
{
    val option = SystemSettings.varchar("option", 255)
    val value  = SystemSettings.varchar("value", 255)
}
