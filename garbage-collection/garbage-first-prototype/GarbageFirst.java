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

    // TODO:: we need a way to determine sources and push them down to the garbage collection core algos.
    void doGc() {
        List<HeapObject> sources = getSources();
        memory.youngGen.doYoungGC(sources);
        if (isOldGenGettingFull()) {
            memory.oldGen.doMark(sources);
            memory.oldGen.doRemark(sources);
            memory.oldGen.doCleanupEmptyRegions();
        }

        // do SR only if needed.
        if (isOldGenGettingFull()) {
            memory.oldGen.doSpaceReclamation();
        }
    }

    List<HeapObject> getSources() {
        // how to get sources.
        // gc should happen at the end of a program statement.
        // stack trace should give you all the current active functions.
        // lets segregate code from data in this discussion, as code can always be reloaded from disk.
        // all the local vars are source objects.
        // all the static objects within the classes involved in the stack trace are also source objects.
        // all objects reachable from source objects are to be marked.
        return new ArrayList<HeapObject>();
    }

    public static void main(String args[]) {
        Memory memory = null;
        GarbageFirst gcAlgorithm = new GarbageFirst(memory, 60, DEFAULT_THREAD_COUNT);

        gcAlgorithm.doGc();
    }
}
