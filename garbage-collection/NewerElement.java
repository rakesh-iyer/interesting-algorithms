import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class NewerElement {
    boolean mark;
    boolean atom;
    int child = -1;
    int sibling = -1;

    NewerElement(boolean a) {
        atom = a;
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
