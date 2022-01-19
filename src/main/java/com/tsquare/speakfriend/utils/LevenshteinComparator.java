package com.tsquare.speakfriend.utils;

import com.trevorthompson.Levenshtein;
import javafx.scene.Node;

import java.util.Comparator;

public class LevenshteinComparator implements Comparator<Node> {
    private String compareTo = "";

    public void setCompareTo(String compareTo) {
        this.compareTo = compareTo;
    }

    @Override
    public int compare(Node a, Node b) {
        Levenshtein levenshtein = new Levenshtein();

        return Float.compare(
            levenshtein.getRatio(b.getId().replace("$:$", " "), compareTo),
            levenshtein.getRatio(a.getId().replace("$:$", " "), compareTo)
        );
    }
}
