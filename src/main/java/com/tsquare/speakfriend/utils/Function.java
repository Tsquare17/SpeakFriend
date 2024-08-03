package com.tsquare.speakfriend.utils;

import java.sql.SQLException;

@FunctionalInterface
public interface Function {
    void apply() throws SQLException;
}
