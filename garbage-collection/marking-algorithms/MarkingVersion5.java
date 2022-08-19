import java.util.ArrayList;
import java.util.List;

public class MarkingVersion5 {
    NewerElement[] memory;
    MarkingVersion5(NewerElement[] elements) {
        memory = elements;
    }

    List<Integer> getSources() {
        List<Integer> sources = new ArrayList<>();

        for (int i = 0; i < memory.length; i++) {
            if (!memory[i].isAtom() && memory[i].isMarked()) {
                sources.add(i);
            }
        }

        return sources;
    }

    void doMarking() {
        int top = -1;

        List<Integer> sources = getSources();

        if (sources.size() == 0) {
            return;
        }

        System.out.println("Iterating sources.");
        for (int source: sources) {
            System.out.println(memory[source]);

            int elementIdx = source; // some source.
            NewerElement element = memory[elementIdx];
            boolean skipFirstDownProcessing = false;
            boolean skipSecondDownProcessing = false;

            do {
                element.setMark();

                if (element.isAtom()) {
                    skipFirstDownProcessing = true;
                    skipSecondDownProcessing = true;
                }

                if (!skipFirstDownProcessing) {
                    int otherElementIdx = element.child;
                    NewerElement otherElement = getMemory(otherElementIdx);
                    if (otherElementIdx != -1 && !otherElement.isMarked()) {
                        element.setAtom();
                        element.child = top;
                        top = elementIdx;
                        element = otherElement;
                        elementIdx = otherElementIdx;
                        continue;
                    }
                }

                if (!skipSecondDownProcessing) {
                    int otherElementIdx = element.sibling;
                    NewerElement otherElement = getMemory(otherElementIdx);
                    if (otherElementIdx != -1 && !otherElement.isMarked()) {
                        element.setAtom();
                        element.sibling = top;
                        top = elementIdx;
                        element = otherElement;
                        elementIdx = otherElementIdx;
                        continue;
                    }
                }

                if (top != -1) {
                    int otherElementIdx = top;
                    NewerElement otherElement = memory[top];
                    if (otherElement.isAtom()) {
                        otherElement.clearAtom();
                        top = otherElement.child;
                        otherElement.child = elementIdx;
                        element = otherElement;
                        elementIdx = otherElementIdx;
                        skipFirstDownProcessing = true;
                        skipSecondDownProcessing = false;
                    } else {
                        top = otherElement.sibling;
                        otherElement.sibling = elementIdx;
                        element = otherElement;
                        elementIdx = otherElementIdx;
                        skipFirstDownProcessing = true;
                        skipSecondDownProcessing = true;
                    }
                }
            } while (top != -1);
        }
    }


    NewerElement getMemory(int index) {
        return index != -1 ? memory[index] : null;
    }
}
