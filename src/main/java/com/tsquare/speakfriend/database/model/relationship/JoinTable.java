package com.tsquare.speakfriend.database.model.relationship;

import java.util.HashMap;

public class JoinTable {
    HashMap<String, String> tableMap= new HashMap<>();

    public void setTable(String table) {
        tableMap.put("table", table);
    }

    public void setKeys(String key, String relatedKey) {
        tableMap.put("key", key);
        tableMap.put("related_key", relatedKey);
    }

    public String getTable() {
        return tableMap.get("table");
    }

    public String getKey() {
        return tableMap.get("key");
    }

    public String getRelatedKey() {
        return tableMap.get("related_key");
    }
}
