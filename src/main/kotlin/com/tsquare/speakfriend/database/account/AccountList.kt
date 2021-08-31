package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.account.preview.AccountPreview
import com.tsquare.speakfriend.auth.Auth
import com.tsquare.speakfriend.auth.CurrentUser
import com.tsquare.speakfriend.crypt.Crypt.decrypt
import com.tsquare.speakfriend.crypt.Crypt.encrypt
import com.tsquare.speakfriend.state.State
import com.tsquare.speakfriend.utils.AccountPreviewComparator

import java.util.ArrayList

class AccountList {
    companion object {
        private var previewList: MutableList<AccountPreview> = ArrayList()
        private var accountList: ArrayList<MutableList<String>> = ArrayList()
        private var stagedImportList: List<MutableList<String>> = ArrayList()

        @JvmStatic
        fun get(userId: Int): List<AccountEntity> {
            return Account().getByUserId(userId)
        }

        @JvmStatic
        fun getByIds(accountIds : List<Int>): List<AccountEntity> {
            return Account().getByIds(accountIds)
        }

        @JvmStatic
        fun getById(accountId : Int): AccountEntity? {
            return Account().getById(accountId)
        }

        @JvmStatic
        fun count(userId: Int): Int {
            return get(userId).size
        }

        @JvmStatic
        fun getPreviews(): MutableList<AccountPreview> {
            if (State.isDirtyAccounts == 0) {
                return previewList
            }

            previewList = ArrayList()

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
            val list = ArrayList<Int>()

            return getDecryptedAccounts(list)
        }

        @JvmStatic
        fun getDecryptedAccounts(list: List<Int>): ArrayList<MutableList<String>> {
            val auth = Auth()
            val accounts: List<AccountEntity>
            val key = auth.getKey()
            accounts = if (list.isEmpty()) {
                get(auth.getId())
            } else {
                getByIds(list)
            }

            accountList = ArrayList()
            // Need to make like associative array to send JSON to API.
            for (account in accounts) {
                val accountId = account.id.value
                var accountName = ""
                var accountUser = ""
                var accountPass = ""
                var accountUrl = ""
                var accountNotes = ""

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

                val addAccount: MutableList<String> = ArrayList()
                addAccount.add(accountId.toString())
                addAccount.add(accountName)
                addAccount.add(accountUser);
                addAccount.add(accountPass);
                addAccount.add(accountUrl);
                addAccount.add(accountNotes);

                accountList.add(addAccount)
            }

            return accountList
        }

        @JvmStatic
        fun lock(accounts: ArrayList<MutableList<String>>, key: String): ArrayList<MutableList<String>> {
            val list = ArrayList<MutableList<String>>()
            for (account in accounts) {
                val accountFields: MutableList<String> = ArrayList()
                for ((i, field) in account.withIndex()) {
                    if (i == 0) {
                        accountFields.add(field)
                    } else {
                        accountFields.add(encrypt(key, field, 2000))
                    }
                };
                list.add(accountFields)
            }

            return list
        }

        @JvmStatic
        fun lock(accounts: ArrayList<MutableList<String>>, key: String, skipKey: Int = 0): ArrayList<MutableList<String>> {
            val list = ArrayList<MutableList<String>>()
            for (account in accounts) {
                val accountFields: MutableList<String> = ArrayList()
                for ((i, field) in account.withIndex()) {
                    if (i == skipKey) {
                        accountFields.add(field)
                    } else {
                        accountFields.add(encrypt(key, field, 2000))
                    }
                };
                list.add(accountFields)
            }

            return list
        }

        @JvmStatic
        fun unlock(accounts: MutableList<MutableList<String>>, key: String): MutableList<MutableList<String>> {
            val list = ArrayList<MutableList<String>>()
            for (account in accounts) {
                val accountFields: MutableList<String> = ArrayList()
                for ((i, field) in account.withIndex()) {
                    accountFields.add(decrypt(key, field, 2000))
                };
                list.add(accountFields)
            }

            return list
        }

        @JvmStatic
        fun stageImports(accounts: List<MutableList<String>>) {
            stagedImportList = accounts
        }

        @JvmStatic
        fun getStagedImports(): List<MutableList<String>> {
            return stagedImportList
        }

        @JvmStatic
        fun clear() {
            previewList = ArrayList()
            accountList = ArrayList()
            stagedImportList = ArrayList()
        }

        @JvmStatic
        fun count(): Int {
            if (State.isDirtyAccounts == 0) {
                return previewList.count()
            }

            previewList = ArrayList()

            val auth = Auth()
            val accounts = get(auth.getId())

            return accounts.count()
        }
    }
}
