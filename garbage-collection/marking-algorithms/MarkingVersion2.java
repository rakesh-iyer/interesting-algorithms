import java.util.ArrayDeque;
import java.util.Deque;

public class MarkingVersion2 {
    static void doMarking(Element[] memory) {
        Deque<Element> stack = new ArrayDeque<>();

        System.out.println("Finding sources.");
        // find the sources implicitly for this iteration.
        for (Element element: memory) {
            if (element.isMarked()) {
                System.out.println(element);
                stack.add(element);
            }
        }

        System.out.println("Start marking.");
        do {
            Element n = stack.removeFirst();
            if (n instanceof ElementList) {
                ElementList next = (ElementList) n;
                if (next.child != -1 && !memory[next.child].isMarked()) {
                    memory[next.child].setMark();
                    stack.addFirst(memory[next.child]);
                }
                if (next.sibling != -1 && !memory[next.sibling].isMarked()) {
                    memory[next.sibling].setMark();
                    stack.addFirst(memory[next.sibling]);
                }
            }
        } while (stack.size() > 0);
    }
}