import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

// this is a proxy representation of a usable object in the heap, so as to be able to collect it.
// all allocations will be in Eden.
// this is to keep this prototype agnostic of the language semantics.
public class HeapObject implements Serializable {
    static final int EMPTY = 0;
    static final int NONEMPTY = 1;
    String regionId;
    int startAddress;
    int size;
    boolean mark;
    int age;
    List<HeapObject> references = new ArrayList<>();

    HeapObject(int size) {
        this.size = size;
    }

    void doMarking() {
        if (mark) {
            return;
        }

        // simple bfs.
        Queue<HeapObject> bfsQueue = new LinkedList<>();
        mark = true;
        bfsQueue.add(this);

        while (bfsQueue.size() > 0) {
            HeapObject heapObject = bfsQueue.remove();

            for (HeapObject reference: heapObject.references) {
                if (!reference.mark) {
                    reference.mark = true;
                    bfsQueue.add(reference);
                }
            }
        }
    }

    void deallocate() {
        // no-op in GC languages.
        // do you need to update the region information and delink from the region object
    }

    static HeapObject alloc(int size) {
        HeapObject heapObject = new HeapObject(size);
        Region region = Region.getRegionOfTypeAndAge(Region.RegionType.Eden, 0, size);
        heapObject.startAddress = region.addObject(heapObject);

        return heapObject;
    }

    static List<HeapObject> filterObjects(List<HeapObject> heapObjects, Region.RegionType regionType) {
        return heapObjects.stream().filter(heapObject -> {
            Region region = Region.allRegions.get(heapObject.regionId);
            return region != null && region.type == regionType;
        }).collect(Collectors.toList());
    }

    public HeapObject clone() {
        HeapObject clone = new HeapObject(size);

        return clone;
    }
}
