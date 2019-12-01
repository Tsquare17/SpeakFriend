package com.tsquare.speakfriend.database.user

import com.tsquare.speakfriend.database.tables.Users
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

class UserEntity(id: EntityID<Int>): Entity<Int>(id) {
    companion object: EntityClass<Int, UserEntity>(Users)

    var name by Users.name
    var pass by Users.pass
}
