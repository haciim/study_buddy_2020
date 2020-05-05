package com.example.studdybuddy.timemanagement;

public abstract class Strategy {
    public final SessionType STRATEGY_TYPE;

    private Strategy() {
        STRATEGY_TYPE = SessionType.NONE;
    }

    public abstract StudyInterval[] getTimeTable();
    public static Strategy GetStrategy(int seconds) {
        return null;
    }
}
