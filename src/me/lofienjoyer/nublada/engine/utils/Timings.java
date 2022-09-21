package me.lofienjoyer.nublada.engine.utils;

import me.lofienjoyer.nublada.Nublada;

import java.util.HashMap;
import java.util.Map;

public class Timings {

    private static final Timings INSTANCE = new Timings();

    private final Map<String, Long> activeTimings;
    private final Map<String, Float> finalizedTimings;

    public Timings() {
        this.activeTimings = new HashMap<>();
        this.finalizedTimings = new HashMap<>();
    }

    public static void startTiming(String tag) {
        INSTANCE.activeTimings.put(tag, System.nanoTime());
    }

    public static void stopTiming(String tag) {
        Long timePassed = INSTANCE.activeTimings.get(tag);
        if (timePassed == null) {
            Nublada.LOG.warning("Tried to stop un-existent timing '" + tag + "'");
            return;
        }
        INSTANCE.activeTimings.remove(tag);
        INSTANCE.finalizedTimings.put(tag, (System.nanoTime() - timePassed) / 100000f);
    }

    public static void flushTimings() {
        if (INSTANCE.activeTimings.size() != 0) {
            Nublada.LOG.warning("Active timings not being stopped: " + INSTANCE.activeTimings);
            INSTANCE.activeTimings.clear();
        }
//        printTimings();
        INSTANCE.finalizedTimings.clear();
    }

    private static void printTimings() {
        System.out.println("Timings:");
        INSTANCE.finalizedTimings.forEach((tag, time) -> {
            System.out.println(" - " + tag + ": " + time + "ms");
        });
    }

}
