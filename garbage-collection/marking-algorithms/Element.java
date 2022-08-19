import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

class Atom extends Element {
    int value;

    Atom(int v) {
        value = v;
    }
}

class ElementList extends Element {
    int child;
    int sibling;

    ElementList(int c, int s) {
        child = c;
        sibling = s;
    }
}

class Element {
    boolean mark;
    boolean atom;

    void clearMark() {
        mark = false;
    }

    void setMark() {
        mark = true;
    }

    boolean isMarked() {
        return mark;
    }

    boolean isAtom() {
        return atom;
    }

    void setAtom() {
        atom = true;
    }

    void clearAtom() {
        atom = false;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}