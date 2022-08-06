class MarkingVersion1a {
    static void doMarking(Element[] memory) {
        int next = 0;
        int end = memory.length;
        int alternate;

        do {
            alternate = next + 1;
            Element nextElement = memory[next];
            if (nextElement.isMarked()) {
                if (nextElement instanceof ElementList) {
                    ElementList list = (ElementList) nextElement;
                    if (list.child != -1 && !memory[list.child].isMarked()) {
                        memory[list.child].setMark();
                        alternate = Math.min(alternate, list.child);
                    }
                    if (list.sibling != -1 && !memory[list.sibling].isMarked()) {
                        memory[list.sibling].setMark();
                        alternate = Math.min(alternate, list.sibling);
                    }
                }
            }
            next = alternate;
        } while (next < end);
    }
}