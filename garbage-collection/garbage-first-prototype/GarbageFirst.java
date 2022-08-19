import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GarbageFirst {
    Memory memory;
    int initiatingHeapOccupancyPercent;
    int numThreads = 10;
    GCExecutionData minorGCExecutionData = new GCExecutionData(numThreads);
    GCExecutionData majorGCExecutionData = new GCExecutionData(numThreads);
    static final int DEFAULT_THREAD_COUNT = 10;

    GarbageFirst(Memory m, int iHOP, int threadCount) {
        memory = m;
        initiatingHeapOccupancyPercent = iHOP;
    }

    boolean isOldGenGettingFull() {
        return memory.oldGen.currentOccupancy > initiatingHeapOccupancyPercent;
    }

    void doGc() {
        memory.youngGen.doYoungGC();
        if (isOldGenGettingFull()) {
            memory.oldGen.doMarkOldGen();
            memory.oldGen.doRemarkOldGen();
            memory.oldGen.doCleanupOldGenEmptyRegions();
        }

        // do SR only if needed.
        if (isOldGenGettingFull()) {
            memory.oldGen.doSpaceReclamation();
        }
    }

    public static void main(String args[]) {
        Memory memory = null;
        GarbageFirst gcAlgorithm = new GarbageFirst(memory, 60, DEFAULT_THREAD_COUNT);

        gcAlgorithm.doGc();
    }
}
