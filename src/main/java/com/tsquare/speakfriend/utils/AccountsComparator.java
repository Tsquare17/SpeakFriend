package com.tsquare.speakfriend.utils;

import com.tsquare.speakfriend.database.entity.AccountEntity;

import java.util.Comparator;

public class AccountsComparator<String extends Comparable<String>> implements Comparator<AccountEntity> {
    @Override
    public int compare(AccountEntity a, AccountEntity b) {
        return a.getName().compareTo(b.getName());
    }
}
