package com.tsquare.speakfriend.database.account

class AccountList {
    companion object {
        @JvmStatic
        fun get(userId: Int): List<AccountEntity> {
            return Account().getByUserId(userId)
        }
    }
}
