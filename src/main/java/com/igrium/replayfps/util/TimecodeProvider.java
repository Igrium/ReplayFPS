package com.igrium.replayfps.util;

public interface TimecodeProvider {
    long getStartTime();
    long getTimePassedWhilePaused();
    boolean getServerWasPaused();
}
