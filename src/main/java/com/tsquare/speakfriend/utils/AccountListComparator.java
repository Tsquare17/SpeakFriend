package com.tsquare.speakfriend.utils;

import java.util.Comparator;
import java.util.List;

public class AccountListComparator<String extends Comparable<String>> implements Comparator<List<String>> {
    @Override
    public int compare(List<String> o1, List<String> o2) {
        return o1.get(1).compareTo(o2.get(1));
    }
}
