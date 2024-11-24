package com.tsquare.speakfriend.utils;

import com.tsquare.levenshtein.Levenshtein;
import javafx.scene.Node;

import java.util.Comparator;

public class AccountSearchComparator implements Comparator<Node> {
    private String compareTo = "";

    public void setCompareTo(String compareTo) {
        this.compareTo = compareTo;
    }

    @Override
    public int compare(Node a, Node b) {
        Levenshtein levenshtein = new Levenshtein();

        String item1 = a.getId().replace("$:$", " ");
        String item2 = b.getId().replace("$:$", " ");

        if (item1.startsWith(compareTo) && !item2.startsWith(compareTo)) {
            return -1;
        } else if (!item1.startsWith(compareTo) && item2.startsWith(compareTo)) {
            return 1;
        } else if (item1.startsWith(compareTo) && item2.startsWith(compareTo)) {
            return 0;
        }

        return Float.compare(
            levenshtein.getRatio(item2, compareTo),
            levenshtein.getRatio(item1, compareTo)
        );
    }
}
