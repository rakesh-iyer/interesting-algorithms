import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

class NewElementList extends NewElement {
    NewElementList(int r, int l) {
        super(1, r, l);
    }
}

class NewElementAtom extends NewElement {
    int value;
    NewElementAtom(int v) {
        super(2);
        value = v;
    }
}

class NewElementListHead extends NewElement {
    NewElementListHead(int l) {
        super(0, -1, l);
    }
}


public class NewElement {
    boolean mark;
    int type;
    int ref = -1;
    int link = -1;

    NewElement(int t) {
        type = t;
    }

    NewElement(int t, int r) {
        type = t;
        ref = r;
    }
    NewElement(int t, int r, int l) {
        type = t;
        ref = r;
        link = l;
    }

    boolean isListHead() {
        return type == 0;
    }

    boolean isListElement() {
        return type == 1;
    }

    boolean isAtomElement() {
        return type > 1;
    }

    void clearMark() {
        mark = false;
    }

    void setMark() {
        mark = true;
    }

    boolean isMarked() {
        return mark;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
