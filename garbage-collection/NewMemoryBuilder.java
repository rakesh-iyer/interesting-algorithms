import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewMemoryBuilder {
    static Random random = new Random();

    static NewElement[] buildMemory(int numMemoryElements) {
        // add extra memory for list heads.
        NewElement[] memory = new NewElement[numMemoryElements*2];
        List<Integer> listHeads = new ArrayList<>();
        List<Integer> listIndexes = new ArrayList<>();
        int i = 0;
        for (int j = 0; j < numMemoryElements; j++) {
            boolean elementType = random.nextBoolean();

            if (elementType) {
                memory[i] = new NewElementListHead(i+1);
                listHeads.add(i);
                memory[i+1] = new NewElementList(-1, -1);
                listIndexes.add(i+1);
                i+=2;
            } else {
                memory[i] = new NewElementAtom(i);
                i++;
            }
        }

        // fill the rest of the memory with atoms.
        for (; i < numMemoryElements * 2; i++) {
            memory[i] = new NewElementAtom(i);
        }

        int size = listHeads.size();
        for (i = 0; i < size; i++) {
            NewElementList list = (NewElementList) memory[listIndexes.get(i)];
            int child = random.nextInt(size);
            if (child != i) {
                list.ref = listHeads.get(child);
            }
        }

        return memory;
    }

    static void clearMark(NewElement[] memory) {
        for (NewElement newElement : memory) {
            newElement.clearMark();
        }
    }

    static void markSources(NewElement[] memory) {
        for (int i = 0; i < memory.length; i++) {
            if (memory[i].isListHead() && random.nextBoolean() && random.nextBoolean()) {
                memory[i].setMark();
            }
        }
    }

    static void printMemoryContents(NewElement[] memory) {
        for (int i = 0; i < memory.length; i++) {
            System.out.println(memory[i].toString());
        }
    }
}
