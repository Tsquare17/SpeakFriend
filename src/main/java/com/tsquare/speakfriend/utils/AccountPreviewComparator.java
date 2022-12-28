package com.tsquare.speakfriend.utils;

import com.tsquare.speakfriend.database.entity.AccountPreviewEntity;

import java.util.Comparator;

public class AccountPreviewComparator<String extends Comparable<String>> implements Comparator<AccountPreviewEntity> {
    @Override
    public int compare(AccountPreviewEntity o1, AccountPreviewEntity o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
