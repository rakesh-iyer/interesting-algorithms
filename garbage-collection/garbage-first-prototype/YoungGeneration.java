import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class YoungGeneration {
    List<Region> edenRegions = new ArrayList<>();
    List<Region> survivor1Regions = new ArrayList<>();
    List<Region> survivor2Regions = new ArrayList<>();
    AtomicBoolean currentSurvivor;
    OldGeneration oldGeneration;
    final static int YOUNG_GEN_NUMBER_OF_REGIONS = 10;

    YoungGeneration(OldGeneration oldGeneration) {
        this.oldGeneration = oldGeneration;
        // allocate memory for the 3 regions.
        for (int i = 0; i < YOUNG_GEN_NUMBER_OF_REGIONS; i++) {
            edenRegions.add(new Region(Region.RegionType.Eden));
        }

        for (int i = 0; i < YOUNG_GEN_NUMBER_OF_REGIONS; i++) {
            survivor1Regions.add(new Region(Region.RegionType.Survivor));
        }

        for (int i = 0; i < YOUNG_GEN_NUMBER_OF_REGIONS; i++) {
            survivor2Regions.add(new Region(Region.RegionType.Survivor));
        }
    }

    void doYoungGC(List<HeapObject> sources) {
        // the steps to collect both eden and current survivor are identical so use a common function.
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
        oldGeneration.copyToRegion(agedData);
        // for both eden and survivor we dont expect to retain any other heap objects.
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

    void markData(List<HeapObject> regionSources) {
        for (HeapObject heapObject: regionSources) {
            heapObject.doMarking();
        }
    }
}
