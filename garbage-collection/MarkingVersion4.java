public class MarkingVersion4 {
    int top;
    NewElement memory[];

    MarkingVersion4(NewElement[] elements) {
        memory = elements;
    }

    void doMarking() {
        // add source nodes to the stack.
        System.out.println("Finding sources.");
        top = -1;
        for (int i = 0; i < memory.length; i++) {
            NewElement newElement = memory[i];
            if (newElement.isListHead() && newElement.isMarked()) {
                System.out.println(newElement);
                newElement.ref = top;
                top = i;
            }
        }

        NewElement newElement = null;
        boolean dontPop = false;
        while (top != -1) {
            if (!dontPop) {
                newElement = memory[top];
                top = newElement.ref;
            }

            if (newElement.link == -1 || memory[newElement.link].isListHead()) {
                dontPop = false;
                continue;
            }

            newElement = memory[newElement.link];
            int linkedRef = newElement.ref;
            newElement.setMark();
            if (newElement.isAtomElement()) {
                // marking the atom information.
 //               memory[linkedRef].setMark();
            } else if (linkedRef != -1 && !memory[linkedRef].isMarked()) { // isListElement. as LH and Atom are addressed before here.
                NewElement refNewElement = memory[linkedRef];

                refNewElement.setMark();
                refNewElement.ref = top;
                top = linkedRef;
            }
            dontPop = true;
        }
    }
}
