package com.tsquare.speakfriend.database.settings

import com.tsquare.speakfriend.auth.CurrentUser
import com.tsquare.speakfriend.database.connection.Conn
import com.tsquare.speakfriend.database.tables.Settings
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception

class Setting {
    init {
        Conn()
    }

    fun create(optionArg: String, valueArg: String) {
        val userIdArg = CurrentUser.userId

        try {
            transaction {
                SettingsEntity.new {
                    userId = userIdArg
                    option = optionArg
                    value = valueArg
                }
            }
        } catch (e: Exception) {
            println("Settings table option already exists.");
        }
    }

    fun update(optionArg: String, valueArg: String) {
        val userId = CurrentUser.userId

        val setting = transaction {
            SettingsEntity.find {
                Settings.option eq optionArg
                Settings.userId eq userId
            }.first()
        }

        transaction {
            SettingsEntity[setting.id].apply {
                value = valueArg
            }
        }
    }

    fun getOption(optionArg: String): SettingsEntity? {
        val userId = CurrentUser.userId
        return transaction {
            SettingsEntity.find {
                Settings.userId eq userId
                Settings.option eq optionArg
            }.first()
        }
    }
}
