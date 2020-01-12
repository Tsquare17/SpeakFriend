package com.tsquare.speakfriend.utils;

import com.tsquare.speakfriend.account.preview.AccountPreview;

import java.util.Comparator;

public class AccountPreviewComparator<String extends Comparable<String>> implements Comparator<AccountPreview> {
    @Override
    public int compare(AccountPreview o1, AccountPreview o2) {
        return o1.getAccountName().compareTo(o2.getAccountName());
    }
}
