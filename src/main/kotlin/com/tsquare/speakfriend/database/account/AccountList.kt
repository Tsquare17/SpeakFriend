package com.tsquare.speakfriend.database.account

class AccountList {
    companion object {
        @JvmField var id : List<Int> = listOf()
        @JvmField var name : List<String?> = listOf()
        @JvmField var url : List<String?> = listOf()
        @JvmField var notes : List<String?> = listOf()

        @JvmStatic
        fun generate(userId: Int): Int {
            val accounts = Account()
            val accountList = accounts.getByUserId(userId)
            this.id    = listOf()
            this.name  = listOf()
            this.url   = listOf()
            this.notes = listOf()
            for (account in accountList) {
                this.id    += account.id.value
                this.name  += account.name
                this.url   += account.url
                this.notes += account.notes
            }
            return accountList.count();
        }

        @JvmStatic
        fun getId(index: Int): Int {
            return this.id[index]
        }

        @JvmStatic
        fun getName(index: Int): String? {
            return this.name[index]
        }

        @JvmStatic
        fun getUrl(index: Int): String? {
            return this.url[index]
        }

        @JvmStatic
        fun getNotes(index: Int): String? {
            return this.notes[index]
        }
    }
}
