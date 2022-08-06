import java.util.ArrayDeque;
import java.util.Deque;

public class MarkingVersion3 {
    int top;
    int bottom;
    int end;
    int nextIndexPotential;
    int stack[];
    int stackSize;
    Element[] memory;

    MarkingVersion3(Element[] elements, int ss) {
        memory = elements;
        stackSize = ss;
        stack = new int[stackSize];
        top = stackSize - 1;
        bottom = stackSize - 1;
        end = elements.length;
        nextIndexPotential = end;
    }
    
    void insertIntoStack(int element) {
        top = ++top % stackSize;
        stack[top] = element;
        if (top == bottom) {
            bottom = ++bottom % stackSize;
            nextIndexPotential = Math.min(nextIndexPotential, stack[bottom]);
        }
    }

    int popFromStack() {
        int element = stack[top];
        // ensure a positive remainder for the non euclidean division.
        top = (--top % stackSize + stackSize) % stackSize;

        return element;
    }

    void doMarking() {
        System.out.println("Finding sources.");
        // find the sources implicitly for this iteration.
        // is it a good assumption that the stack atleast needs space for
        // the sources, or initial sources.
        // if there are too many sources maybe we could mark them in batches.
        for (int i = 0; i < memory.length; i++) {
            if (memory[i].isMarked()) {
                System.out.println(memory[i]);
                insertIntoStack(i);
            }
        }

        int nextIndex = -1;
        boolean dontPop = false;
        do {
            if (top != bottom) {
                if (!dontPop) {
                    nextIndex = popFromStack();
                }
                if (memory[nextIndex] instanceof ElementList) {
                    ElementList next = (ElementList) memory[nextIndex];
                    if (next.child != -1 && !memory[next.child].isMarked()) {
                        memory[next.child].setMark();
                        insertIntoStack(next.child);
                    }
                    if (next.sibling != -1 && !memory[next.sibling].isMarked()) {
                        memory[next.sibling].setMark();
                        insertIntoStack(next.sibling);
                    }
                    dontPop = false;
                    continue;
                } else {
                    dontPop = false;
                    continue;
                }
            }
            while (nextIndexPotential < end && (memory[nextIndexPotential] instanceof Atom || !memory[nextIndexPotential].isMarked())) {
                nextIndexPotential++;
            }

            // if nextIndexPotential >= end this will exit irrespective, so remove the unimpactful (nextIndexPotential < end) check.
            if (nextIndexPotential < end && memory[nextIndexPotential].isMarked()) {
                nextIndex = nextIndexPotential;
                nextIndexPotential++;
                dontPop = true;
            } else if (nextIndexPotential >= end) {
                break;
            }
        } while (true);
    }
}