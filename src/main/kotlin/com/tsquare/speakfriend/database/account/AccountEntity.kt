package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.database.tables.Accounts
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

class AccountEntity(id: EntityID<Int>): Entity<Int>(id) {
    companion object: EntityClass<Int, AccountEntity>(Accounts)

    var userId by Accounts.userId
    var name by Accounts.name
    var user by Accounts.user
    var pass by Accounts.pass
    var url by Accounts.url
    var notes by Accounts.notes
}
