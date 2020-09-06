package com.tsquare.speakfriend.settings

import com.tsquare.speakfriend.database.system.System
import com.tsquare.speakfriend.database.system.SystemEntity

class SystemSettings {
    companion object {
        @JvmStatic
        fun get(option: String): String {
            val system = System()
            val systemValue = system.getOption(option)

            return systemValue?.value ?: ""
        }

        @JvmStatic
        fun put(option: String, value: String) {
            val systemSetting = System()

            if (systemSetting.getOption(option) is SystemEntity) {
                systemSetting.update(option, value)
            } else {
                systemSetting.create(option, value)
            }
        }
    }
}