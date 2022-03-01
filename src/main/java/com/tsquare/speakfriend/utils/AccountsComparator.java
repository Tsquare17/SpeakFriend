package com.tsquare.speakfriend.utils;

import com.tsquare.speakfriend.account.Account;

import java.util.Comparator;

public class AccountsComparator<String extends Comparable<String>> implements Comparator<Account> {
    @Override
    public int compare(Account a, Account b) {
        return a.getName().compareTo(b.getName());
    }
}
