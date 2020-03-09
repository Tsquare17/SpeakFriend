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
        val setting = this.getOption(optionArg)

        if (setting != null) {
            transaction {
                SettingsEntity[setting.id].apply {
                    value = valueArg
                }
            }
        }
    }

    fun delete(optionArg: String) {
        val setting = this.getOption(optionArg)

        if (setting != null) {
            transaction {
                SettingsEntity[setting.id].delete()
            }
        }
    }

    fun getOption(optionArg: String): SettingsEntity? {
        val userId = CurrentUser.userId

        return transaction {
            SettingsEntity.find {
                Settings.userId eq userId
                Settings.option eq optionArg
            }.firstOrNull()
        }
    }
}
