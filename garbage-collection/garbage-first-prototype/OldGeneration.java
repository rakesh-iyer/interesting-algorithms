import java.util.*;
import java.util.stream.Stream;

public class OldGeneration {
    List<Region> humunguous;
    List<Region> normal;
    int currentOccupancy;
    static final int OLD_GEN_PROMOTION_AGE = 10; // 10 minor cycles lads to promotion.
    static final double liveThresholdFactor = 0.85;

    void doMarkOldGen() {
        List<HeapObject> sources = getOldGenSources();
        // snapshot memory and then mark it using one of the marking algorithms.
        // mark the data in this region.
        List<HeapObject> regionSources = HeapObject.filterObjects(sources, Region.RegionType.Old);
        markData(regionSources);
    }

    List<HeapObject> getOldGenSources() {
        // how to get sources.
        // gc should happen at the end of a program statement.
        // stack trace should give you all the current active functions.
        // lets segregate code from data in this discussion, as code can always be reloaded from disk.
        // all the local vars are source objects.
        // all the static objects within the classes involved in the stack trace are also source objects.
        // all objects reachable from source objects are to be marked.
        return new ArrayList<HeapObject>();
    }

    void doRemarkOldGen() {
        // we need to hone into this later, this might be the most detail oriented of the algorithms involved.
        List<HeapObject> sources = getOldGenSources();
        // snapshot memory and then mark it using one of the marking algorithms.
        // mark the data in this region.
        List<HeapObject> regionSources = HeapObject.filterObjects(sources, Region.RegionType.Old);
        markData(regionSources);
    }

    void doCleanupOldGenEmptyRegions() {
        List<Region> regions = Region.getRegionsOfType(Region.RegionType.Old);

        for (Iterator<Region> regionIterator = regions.iterator(); regionIterator.hasNext();) {
            Region region = regionIterator.next();
            if (region.size.get() == 0) {
                region.deallocate();
                regionIterator.remove();
            }
        }
    }

    Stream<Region> getOldGenGCCandidateRegions() {
        return Region.getRegionsOfType(Region.RegionType.Old)
                .stream()
                .filter(region -> region.size.get() < liveThresholdFactor * Region.MAX_SIZE);
    }

    void doSpaceReclamation() {
        // this is iterative and time bounded.
        // keep track of pause time.
        // startPause = System.currentTimeMillis();
        // endPause = System.currentTimeMillis();
        // move objects to different regions i.e. copy-compact.
        Stream<Region> candidateRegions = getOldGenGCCandidateRegions();
        for (Iterator<Region> regionIterator = candidateRegions.iterator(); regionIterator.hasNext();) {
            List<HeapObject> heapObjects = Region.filterMarkedObjects(regionIterator.next());
            // copy these heap
            copyToOldGen(heapObjects);
        }
    }

    void copyToOldGen(List<HeapObject> heapObjects) {
        for (HeapObject heapObject: heapObjects) {
            Region region = Region.getRegionsOfTypeAndAge(Region.RegionType.Old, heapObject.age, heapObject.size);
            if (region.canAddObject(heapObject)) {
                region.addObject(heapObject);
            } else {
                // we will handle out of region memory later.
                throw new RuntimeException();
            }
        }
    }

    void markData(List<HeapObject> edenSources) {
        for (HeapObject heapObject: edenSources) {
            heapObject.doMarking();
        }
    }
}

