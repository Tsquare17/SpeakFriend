package com.tsquare.speakfriend.database.account

import com.tsquare.speakfriend.account.preview.AccountPreview
import com.tsquare.speakfriend.auth.Auth
import com.tsquare.speakfriend.crypt.Crypt.decrypt
import com.tsquare.speakfriend.state.State
import com.tsquare.speakfriend.utils.AccountPreviewComparator

class AccountList {
    companion object {
        private var previewList: MutableList<AccountPreview> = java.util.ArrayList()

        @JvmStatic
        fun get(userId: Int): List<AccountEntity> {
            return Account().getByUserId(userId)
        }

        @JvmStatic
        fun get(): MutableList<AccountPreview> {
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
    }
}
