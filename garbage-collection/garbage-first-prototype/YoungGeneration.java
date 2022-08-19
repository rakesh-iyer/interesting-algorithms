import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class YoungGeneration {
    List<Region> edenRegions;
    List<Region> survivor1Regions;
    List<Region> survivor2Regions;
    AtomicBoolean currentSurvivor;
    OldGeneration oldGeneration;
    List<HeapObject> findYoungGenSources() {
        return new ArrayList<>();
    }

    YoungGeneration(OldGeneration oldGeneration) {
        this.oldGeneration = oldGeneration;
    }

    void doYoungGC() {
        List<HeapObject> sources = findYoungGenSources();
        doYoungGCSingleSpace(sources, Region.RegionType.Eden);
        doYoungGCSingleSpace(sources, Region.RegionType.Survivor);
        // switch next survivor to current survivor.
        switchSurvivorSpace();
    }

    void switchSurvivorSpace() {
        currentSurvivor.set(!currentSurvivor.get());
    }

    void doYoungGCSingleSpace(List<HeapObject> sources, Region.RegionType regionType) {
        // mark the data in this region.
        List<HeapObject> regionSources = HeapObject.filterObjects(sources, regionType);
        markData(regionSources);
        // move the marked heap objects to next survivor.
        // delete all other objects in the region
        List<HeapObject> markedData = Region.filterMarkedObjects(regionType);
        Region.deAllocateUnmarkedObjects(regionType);
        // the main value of this vs sweeping is to avoid internal defrag costs.
        List<HeapObject> agedData = copyToNextSurvivor(markedData);
        oldGeneration.copyToOldGen(agedData);
        Region.clearAllObjects(regionType);
    }

    List<Region> getCurrentSurvivorRegions() {
        return currentSurvivor.get() ? survivor1Regions : survivor2Regions;
    }

    List<HeapObject> copyToNextSurvivor(List<HeapObject> heapObjects) {
        List<Region> nextSurvivor = getCurrentSurvivorRegions();
        int currentRegionIndex = 0;
        Region nextSurvivorRegion = nextSurvivor.get(currentRegionIndex);
        List<HeapObject> agedData = new ArrayList<>();

        for (HeapObject heapObject: heapObjects) {
            heapObject.age++;
            if (heapObject.age >= OldGeneration.OLD_GEN_PROMOTION_AGE) {
            } else if (nextSurvivorRegion.canAddObject(heapObject)) {
                nextSurvivorRegion.addObject(heapObject);
            } else if (currentRegionIndex >= nextSurvivor.size()) {
                // TODO:: this allocate shall fail at some point, add handling.
                nextSurvivorRegion = Region.allocateRegion(nextSurvivorRegion.type);
                nextSurvivor.add(nextSurvivorRegion);
                currentRegionIndex++;
            }
        }

        return agedData;
    }

    void markData(List<HeapObject> edenSources) {
        for (HeapObject heapObject: edenSources) {
            heapObject.doMarking();
        }
    }
}
