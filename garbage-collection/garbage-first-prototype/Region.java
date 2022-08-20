import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Region {
    String regionId;
    ConcurrentLinkedQueue<HeapObject> heapObjects;
    AtomicInteger size;
    RegionType type;
    int age;
    byte regionData[] = new byte[MAX_SIZE];
    // there might be internal fragmentation in real cases.
    static final int MAX_SIZE = 65536;
    static Map<RegionType, List<Region>> regionsMap = new HashMap<>();
    static Map<String, Region> allRegions = new HashMap<>();

    enum RegionType {
        Eden,
        Survivor,
        Humungous,
        Old
    }

    Region(RegionType t) {
        regionId = UUID.randomUUID().toString();
        type = t;
        regionsMap.putIfAbsent(type, new ArrayList<>());
        regionsMap.get(type).add(this);
        allRegions.put(regionId, this);
    }

    static class RegionComparator implements Comparator<Region> {
        @Override
        public int compare(Region o1, Region o2) {
            if (o1.age == o2.age) {
                return o1.size.get() - o2.size.get();
            } else {
                return o1.age - o2.age;
            }
        }
    }

    void deallocate() {
        // deallocate.
        regionsMap.get(type).remove(this);
    }

     // lets remove these monitor calls.
    synchronized boolean canAddObject(HeapObject heapObject) {
        int testSize = size.get() + heapObject.size;
        return testSize > MAX_SIZE;
    }

    synchronized int addObject(HeapObject heapObject) {
        heapObjects.add(heapObject);
        return size.getAndAdd(heapObject.size);
    }

    // do not change the size as it will require managing internal fragmentation.
    // better to rely on copying to a new region on a collection.
    // lets confirm this is how Garbage-First does this.
    synchronized void removeObject(HeapObject heapObject) {
        heapObjects.remove(heapObject);
    }

    static void deAllocateUnmarkedObjects(Region.RegionType regionType) {
        List<Region> regions = regionsMap.get(regionType);
        regions.stream().flatMap(region -> region.heapObjects.stream()).filter(
                heapObject -> !heapObject.mark).forEach(heapObject -> heapObject.deallocate());
    }

    static void clearAllObjects(Region.RegionType regionType) {
        List<Region> regions = regionsMap.get(regionType);
        for (Region region: regions) {
            region.heapObjects.clear();
        }
    }

    static List<HeapObject> filterMarkedObjects(Region region) {
        return region.heapObjects.stream().filter(
                heapObject -> heapObject.mark).collect(Collectors.toList());
    }

    static List<HeapObject> filterMarkedObjects(Region.RegionType regionType) {
        List<Region> regions = regionsMap.get(regionType);
        return regions.stream().flatMap(region -> region.heapObjects.stream()).filter(
                heapObject -> heapObject.mark).collect(Collectors.toList());
    }

    static Region allocateRegion(RegionType regionType) {
        return new Region(regionType);
    }

    public static List<Region> getRegionsOfType(RegionType regionType) {
        return regionsMap.get(regionType);
    }

    // lets do suboptimal for now, and then try to find the right datastructure for this.
    // what complicates things is querying age based region Identification is Old Gen specific.
    public static Region getRegionOfTypeAndAge(RegionType regionType, int age, int space) {
        List<Region> regions = regionsMap.get(regionType);

        for (Region region: regions) {
            if (region.age == age && region.size.get() + space < MAX_SIZE) {
                return region;
            }
        }

        Region region = new Region(regionType);
        region.age  = age;
        return region;
    }

    boolean isEmpty() {
        return heapObjects.size() == 0;
    }
}
