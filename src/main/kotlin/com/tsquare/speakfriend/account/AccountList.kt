package com.tsquare.speakfriend.account

import com.tsquare.speakfriend.auth.CurrentUser
import com.tsquare.speakfriend.crypt.Crypt.decrypt
import com.tsquare.speakfriend.database.account.AccountList.Companion.get
import kotlin.collections.ArrayList

class AccountList {
    var accounts: MutableList<Account> = ArrayList()
    var locked = true
    var unlocked = !locked

    fun set(accountList: MutableList<Account>) {
        accounts = accountList
    }

    fun get(): List<Account> {
        return accounts
    }

    fun unlock(key: String): MutableList<Account> {
        if (unlocked) {
            return accounts
        }

        // Get all accounts for the current user.
        val rawAccounts = get(CurrentUser.userId)

        for (account in rawAccounts) {
            val accountId = account.id.value
            var accountName = ""
            var accountUser = ""
            var accountPass = ""
            var accountUrl = ""
            var accountNotes = ""

            try {
                accountName = decrypt(key, account.name)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                accountUser = decrypt(key, account.user)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                accountPass = decrypt(key, account.pass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                accountUrl = decrypt(key, account.url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                accountNotes = decrypt(key, account.notes)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val decryptedAccount = Account(accountId, accountName, accountUser, accountPass, accountUrl, accountNotes)
            accounts.add(decryptedAccount)
        }

        return accounts
    }
}
