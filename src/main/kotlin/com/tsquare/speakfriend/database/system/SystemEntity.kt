package com.tsquare.speakfriend.database.system

import com.tsquare.speakfriend.database.tables.SystemSettings
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

class SystemEntity(id: EntityID<Int>): Entity<Int>(id) {
    companion object: EntityClass<Int, SystemEntity>(SystemSettings)

    var option by SystemSettings.option
    var value by SystemSettings.value
}
