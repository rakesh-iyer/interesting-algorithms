import java.util.*;
import java.util.stream.Stream;

public class OldGeneration {
    List<Region> humunguous;
    List<Region> normal;
    int currentOccupancy;
    static final int OLD_GEN_PROMOTION_AGE = 10; // 10 minor cycles lads to promotion.
    static final double liveThresholdFactor = 0.85;

    void doMark(List<HeapObject> sources) {
        // snapshot memory and then mark it using one of the marking algorithms.
        // we need to essentially create a copy of the heap objects. But is this in old gen only??
        // do we need to pause on the copy creation?
        List<HeapObject> copySources = copyHeapSources(sources);
        List<HeapObject> regionCopySources = HeapObject.filterObjects(copySources, Region.RegionType.Old);
        markData(regionCopySources);
    }

    void doRemark(List<HeapObject> sources) {
        // we need to hone into this later, this might be the most detail oriented of the algorithms involved.
        // this is a pause.
        List<HeapObject> regionSources = HeapObject.filterObjects(sources, Region.RegionType.Old);
        markData(regionSources);
    }

    void doCleanupEmptyRegions() {
        List<Region> regions = Region.getRegionsOfType(Region.RegionType.Old);

        for (Iterator<Region> regionIterator = regions.iterator(); regionIterator.hasNext();) {
            Region region = regionIterator.next();
            if (region.isEmpty()) {
                region.deallocate();
                regionIterator.remove();
            }
        }
    }

    Stream<Region> getGCCandidateRegions() {
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
        Stream<Region> candidateRegions = getGCCandidateRegions();
        for (Iterator<Region> regionIterator = candidateRegions.iterator(); regionIterator.hasNext();) {
            List<HeapObject> heapObjects = Region.filterMarkedObjects(regionIterator.next());
            // copy these heap
            copyToRegion(heapObjects);
        }
    }

    void copyToRegion(List<HeapObject> heapObjects) {
        for (HeapObject heapObject: heapObjects) {
            Region region = Region.getRegionOfTypeAndAge(Region.RegionType.Old, heapObject.age, heapObject.size);
            if (region.canAddObject(heapObject)) {
                region.addObject(heapObject);
            } else {
                // we will handle out of region memory later.
                throw new RuntimeException();
            }
        }
    }

    void markData(List<HeapObject> regionSources) {
        for (HeapObject heapObject: regionSources) {
            heapObject.doMarking();
        }
    }

    List<HeapObject> copyHeapSources(List<HeapObject> sources) {
        List<HeapObject> copySources = new ArrayList<>();

        for (HeapObject source : sources) {
            HeapObject heapObject = copyHeapSource(source);
            copySources.add(heapObject);
        }

        return copySources;
    }

    HeapObject copyHeapSource(HeapObject source) {
        // use bfs on the sources to avoid wasting too much stack space.
        Queue<HeapObject> queue = new LinkedList<>();
        Map<HeapObject, HeapObject> cloneMap = new HashMap<>();
        Set<HeapObject> visited = new HashSet<>();

        HeapObject clone = source.clone();
        cloneMap.put(source, clone);
        visited.add(source);
        queue.add(source);

        while (!queue.isEmpty()) {
            HeapObject current = queue.remove();

            for (HeapObject reference: current.references) {
                HeapObject referenceClone;
                if (visited.contains(reference)) {
                    referenceClone = reference.clone();

                    cloneMap.put(reference, referenceClone);
                    visited.add(reference);
                    queue.add(reference);
                } else {
                    referenceClone = cloneMap.get(reference);
                }
                current.references.add(referenceClone);
            }
        }

        return clone;
    }

}

