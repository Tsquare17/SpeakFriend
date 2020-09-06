package com.tsquare.speakfriend.database.system

import com.tsquare.speakfriend.database.connection.Conn
import com.tsquare.speakfriend.database.tables.SystemSettings
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception

class System {
    init {
        Conn()
    }

    fun create(optionArg: String, valueArg: String) {
        try {
            transaction {
                SystemEntity.new {
                    option = optionArg
                    value = valueArg
                }
            }
        } catch (e: Exception) {
            println("System option already exists.");
        }
    }

    fun update(optionArg: String, valueArg: String) {
        val system = this.getOption(optionArg)

        if (system != null) {
            transaction {
                SystemEntity[system.id].apply {
                    value = valueArg
                }
            }
        }
    }

    fun delete(optionArg: String) {
        val system = this.getOption(optionArg)

        if (system != null) {
            transaction {
                SystemEntity[system.id].delete()
            }
        }
    }

    fun getOption(optionArg: String): SystemEntity? {
        return transaction {
            SystemEntity.find {
                SystemSettings.option eq optionArg
            }.firstOrNull()
        }
    }
}