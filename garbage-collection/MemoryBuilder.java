import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryBuilder {
    static Random random = new Random();

    static Element[] buildMemory(int numMemoryElements) {
        Element[] memory = new Element[numMemoryElements];
        List<Integer> listIndexes = new ArrayList<>();

        for (int i = 0; i < numMemoryElements; i++) {
            boolean elementType = random.nextBoolean();

            if (elementType) {
                memory[i] = new ElementList(-1, -1);
                listIndexes.add(i);
            } else {
                memory[i] = new Atom(i);
            }
        }

        int size = listIndexes.size();
        for (int i = 0; i < size; i++) {
            ElementList list = (ElementList)memory[listIndexes.get(i)];
            int child = random.nextInt(size);
            if (child != i) {
                list.child = listIndexes.get(child);
            }
        }

        return memory;
    }

    static void clearMark(Element[] memory) {
        for (Element element : memory) {
            element.clearMark();
        }
    }

    static void markSources(Element[] memory) {
        for (int i = 0; i < memory.length; i++) {
            if (memory[i] instanceof ElementList && random.nextBoolean() && random.nextBoolean()) {
                memory[i].setMark();
            }
        }
    }

    static void printMemoryContents(Element[] memory) {
        for (int i = 0; i < memory.length; i++) {
            System.out.println(memory[i].toString());
        }
    }
}
