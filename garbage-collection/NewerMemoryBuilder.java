import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewerMemoryBuilder {
    static Random random = new Random();

    static NewerElement[] buildMemory(int numMemoryNewerElements) {
        NewerElement[] memory = new NewerElement[numMemoryNewerElements];
        List<Integer> listIndexes = new ArrayList<>();

        for (int i = 0; i < numMemoryNewerElements; i++) {
            boolean elementType = random.nextBoolean();

            if (elementType) {
                memory[i] = new NewerElement(false);
                listIndexes.add(i);
            } else {
                memory[i] = new NewerElement(true);
            }
        }

        int size = listIndexes.size();
        for (int i = 0; i < size; i++) {
            NewerElement list = memory[listIndexes.get(i)];
            int child = random.nextInt(size);
            if (child != i) {
                list.child = listIndexes.get(child);
            }
        }

        return memory;
    }

    static void clearMark(NewerElement[] memory) {
        for (NewerElement element : memory) {
            element.clearMark();
        }
    }

    static void markSources(NewerElement[] memory) {
        for (int i = 0; i < memory.length; i++) {
            if (!memory[i].isAtom() && random.nextBoolean() && random.nextBoolean()) {
                memory[i].setMark();
            }
        }
    }

    static void printMemoryContents(NewerElement[] memory) {
        for (int i = 0; i < memory.length; i++) {
            System.out.println(memory[i].toString());
        }
    }
}
