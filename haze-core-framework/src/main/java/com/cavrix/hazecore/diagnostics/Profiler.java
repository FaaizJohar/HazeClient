package com.cavrix.hazecore.diagnostics;

import java.util.HashMap;
import java.util.Map;

public class Profiler {
    private final Map<String, Long> startTimes = new HashMap<>();
    private final Map<String, Long> recordedTimes = new HashMap<>();

    public void startSection(String section) {
        startTimes.put(section, System.nanoTime());
    }

    public void endSection(String section) {
        Long start = startTimes.remove(section);
        if (start != null) {
            long duration = System.nanoTime() - start;
            recordedTimes.put(section, duration);
        }
    }

    public long getRecordedTime(String section) {
        return recordedTimes.getOrDefault(section, 0L);
    }
    
    public void dump() {
        System.out.println("[HazeCore] Profiler Dump:");
        for (Map.Entry<String, Long> entry : recordedTimes.entrySet()) {
            System.out.println(" - " + entry.getKey() + ": " + (entry.getValue() / 1_000_000.0) + "ms");
        }
    }
}
