package com.tsquare.speakfriend.database.settings

import com.tsquare.speakfriend.database.tables.Settings
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

class SettingsEntity(id: EntityID<Int>): Entity<Int>(id) {
    companion object: EntityClass<Int, SettingsEntity>(Settings)

    var userId by Settings.userId
    var option by Settings.option
    var value by Settings.value
}
