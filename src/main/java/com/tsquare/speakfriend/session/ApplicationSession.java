package com.tsquare.speakfriend.session;

public final class ApplicationSession {
    private static ApplicationSession instance;

    private boolean isDirtyAccounts = true;
    private String loadingMessage = "";
    private int selectedAccountId = 0;
    private String exportFileString = "";
    private String accountSearchString = "";

    private ApplicationSession() {}

    public static ApplicationSession getInstance() {
        if (instance == null) {
            instance = new ApplicationSession();
        }

        return instance;
    }

    public boolean isDirtyAccounts() {
        return isDirtyAccounts;
    }

    public void setDirtyAccounts(boolean dirtyAccounts) {
        isDirtyAccounts = dirtyAccounts;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public int getSelectedAccountId() {
        return selectedAccountId;
    }

    public void setSelectedAccountId(int selectedAccountId) {
        this.selectedAccountId = selectedAccountId;
    }

    public String getExportFileString() {
        return exportFileString;
    }

    public void setExportFileString(String exportFileString) {
        this.exportFileString = exportFileString;
    }

    public String getAccountSearchString() {
        return accountSearchString;
    }

    public void setAccountSearchString(String accountSearchString) {
        this.accountSearchString = accountSearchString;
    }

    public void clear() {
        instance = null;
    }
}
