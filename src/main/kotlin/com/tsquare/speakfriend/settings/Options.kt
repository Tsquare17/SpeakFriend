package com.tsquare.speakfriend.settings

import com.tsquare.speakfriend.database.settings.Setting
import com.tsquare.speakfriend.database.settings.SettingsEntity

class Options {
    companion object {
        @JvmStatic
        fun get(option: String): String {
            val setting = Setting()
            val settingEntity = setting.getOption(option)

            return settingEntity?.value ?: ""
        }

        @JvmStatic
        fun put(option: String, value: String) {
            val setting = Setting()

            if (setting.getOption(option) is SettingsEntity) {
                setting.update(option, value)
            } else {
                setting.create(option, value)
            }
        }
    }
}
