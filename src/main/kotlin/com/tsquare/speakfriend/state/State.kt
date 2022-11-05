package com.tsquare.speakfriend.state

object State {
    @JvmStatic
    var isDirtyAccounts = 1

    @JvmStatic
    var loadingMessage = ""

    @JvmStatic
    var selectedAccountId = 0

    @JvmStatic
    var exportFileString = ""

    @JvmStatic
    var accountSearchString = ""

    @JvmStatic
    fun clear() {
        isDirtyAccounts = 1
        loadingMessage = ""
        selectedAccountId = 0
        exportFileString = ""
        accountSearchString = ""
    }
}
