package com.igrium.replayfps.core.util;

public interface TimecodeProvider {
    long getStartTime();
    long getTimePassedWhilePaused();
    boolean getServerWasPaused();
}
