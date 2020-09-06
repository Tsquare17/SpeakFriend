package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.account.preview.AccountPreview
import com.tsquare.speakfriend.auth.Auth
import com.tsquare.speakfriend.crypt.Crypt.decrypt
import com.tsquare.speakfriend.crypt.Crypt.encrypt
import com.tsquare.speakfriend.state.State
import com.tsquare.speakfriend.utils.AccountPreviewComparator

import java.util.ArrayList

class AccountList {
    companion object {
        private var previewList: MutableList<AccountPreview> = ArrayList()
        private var accountList: ArrayList<MutableList<String>> = ArrayList()

        @JvmStatic
        fun get(userId: Int): List<AccountEntity> {
            return Account().getByUserId(userId)
        }

        @JvmStatic
        fun getPreviews(): MutableList<AccountPreview> {
            if (State.isDirtyAccounts == 0) {
                return previewList
            }

            val auth = Auth()
            val accounts = get(auth.getId())
            val key = auth.getKey()

            // Collect list of decrypted account previews.
            for (account in accounts) {
                val accountId = account.id.value
                var accountName = ""
                try {
                    accountName = decrypt(key, account.name)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewList.add(AccountPreview(accountId, accountName))
            }

            // Sort the list of accounts by name.
            previewList.sortWith(AccountPreviewComparator<String>())

            State.isDirtyAccounts = 0

            return previewList
        }

        @JvmStatic
        fun getDecryptedAccounts(): ArrayList<MutableList<String>> {
            val auth = Auth()
            val accounts = get(auth.getId())
            val key = auth.getKey()

            // Need to make like associative array to send JSON to API.
            for (account in accounts) {
                val accountId = account.id.value
                var accountName = ""
                var accountUser = ""
                var accountPass = ""
                var accountUrl = ""
                var accountNotes = ""
                var accountCloudId = 0

                try {
                    accountName = decrypt(key, account.name)
                } catch (e: Exception) {}
                try {
                    accountUser = decrypt(key, account.user)
                } catch (e: Exception) {}
                try {
                    accountPass = decrypt(key, account.pass)
                } catch (e: Exception) {}
                try {
                    accountUrl = decrypt(key, account.url)
                } catch (e: Exception) {}
                try {
                    accountNotes = decrypt(key, account.notes)
                } catch (e: Exception) {}
                try {
                    accountCloudId = account.cloudId!!
                } catch (e: Exception) {}

                val addAccount: MutableList<String> = ArrayList()
                addAccount.add(accountId.toString())
                addAccount.add(accountName)
                addAccount.add(accountUser);
                addAccount.add(accountPass);
                addAccount.add(accountUrl);
                addAccount.add(accountNotes);
                addAccount.add(accountCloudId.toString());

                accountList.add(addAccount)
            }

            return accountList
        }

        @JvmStatic
        fun lock(accounts: ArrayList<MutableList<String>>, key: String): ArrayList<MutableList<String>> {
            val list = ArrayList<MutableList<String>>()
            for (account in accounts) {
                val accountFields: MutableList<String> = ArrayList()
                for (field in account) {
                    accountFields.add(encrypt(key, field))
                };
                list.add(accountFields)
            }

            return list
        }
    }
}
