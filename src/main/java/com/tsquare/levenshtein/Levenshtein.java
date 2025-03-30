package com.tsquare.levenshtein;

import java.util.Arrays;
import java.util.Locale;

public class Levenshtein {
    private boolean ignoreCapitalization = true;

    public int getDistance(String a, String b) {
        return levenshtein(a, b, -1, Integer.MAX_VALUE, 1);
    }

    public int getDistance(String a, String b, int maxDistance) {
        return levenshtein(a, b, maxDistance, Integer.MAX_VALUE, 1);
    }

    public int getDistance(String a, String b, int maxDistance, int maxCheckLength) {
        return levenshtein(a, b, maxDistance, maxCheckLength, 1);
    }

    public float getRatio(String a, String b) {
        return ratio(a, b, -1, Integer.MAX_VALUE);
    }

    public float getRatio(String a, String b, int maxDistance) {
        return ratio(a, b, maxDistance, Integer.MAX_VALUE);
    }

    public float getRatio(String a, String b, int maxDistance, int maxCheckLength) {
        return ratio(a, b, maxDistance, maxCheckLength);
    }

    private float ratio(String a, String b, int maxDistance, int maxCheckLength) {
        int combinedLength = a.length() + b.length();

        return (float) (combinedLength - levenshtein(a, b, maxDistance, maxCheckLength, 2)) / combinedLength;
    }

    private int levenshtein(String a, String b, int maxDistance, int maxCheckLength, int replacementCost) {
        if (this.ignoreCapitalization) {
            a = a.toLowerCase(Locale.ROOT);
            b = b.toLowerCase(Locale.ROOT);
        }

        if (a.equals(b)) {
            return 0;
        }

        int lengthA = a.length();
        int lengthB = b.length();

        int[][] cost = new int[lengthA + 1][lengthB + 1];

        int indexA = 0;
        int indexB = 0;

        costLoop:
        for (int i = 0; i <= lengthA && i < maxCheckLength; i++) {
            for (int j = 0; j <= lengthB && j < maxCheckLength; j++) {
                if (i == 0) {
                    cost[i][j] = j;

                    continue;
                }

                if (j == 0) {
                    cost[i][j] = i;

                    continue;
                }

                int distance = min(
                    cost[i - 1][j] + 1,
                    cost[i][j - 1] + 1,
                    cost[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : replacementCost)
                );

                cost[i][j] = distance;

                if (maxDistance == distance) {
                    indexB = j;
                    indexA = i;

                    break costLoop;
                }
            }
        }

        return (maxDistance < 0 || maxDistance > lengthA || maxDistance > lengthB)
            ? cost[lengthA][lengthB]
            : cost[indexA][indexB];
    }

    private int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(0);
    }

    public void setIgnoreCapitalization(boolean ignoreCapitalization) {
        this.ignoreCapitalization = ignoreCapitalization;
    }
}
