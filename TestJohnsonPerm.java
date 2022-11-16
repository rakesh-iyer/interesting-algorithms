import lombok.*;
import java.util.*;

class TestJohnsonPerm {
    @Setter @Getter
    int n;
    @Setter @Getter
    int k;
    int enumeration[];
    char num[];
    Map<String, Boolean> permMap = new HashMap<>();
    boolean debugging;

    TestJohnsonPerm(int n) {
        setN(n);
        setK(n);
        enumeration = new int[n];
        num = new char[n];
        for (int i = 1; i <= n; i++) {
            num[i-1] = (char)('a' + i);
        }
        enumeration[getN()-1] = 1;
    }

    int getA() {
        int enumKMinus3 = getK() > 2 ? enumeration[getK()-3] : 0;

        if ((enumeration[getK()-2] + (getK()-1) * enumKMinus3) % 2 == 0) {
            return getK() - enumeration[getK()-1];
        } else {
            return enumeration[getK()-1];
        }
    }

    int getB() {
        if (getK() == getN()) {
            return 0;
        } else if (getK() == getN()-1) {
            int enumNMinus3 = getN() > 2 ? enumeration[getN()-3] : 0;
            return (enumeration[getN()-2] + (getN()-1) * enumNMinus3) % 2 == 0 ? 0 : 1;
        } else {
            if (k % 2 == 0) {
                return enumeration[getK() - 1] % 2 == 0 ? 0 : 2;
            } else {
                return (enumeration[getK() - 1] + enumeration[getK() - 2]) % 2 == 0 ? 0 : 1;
            }
        }
    }

    int findNextTransposition() {
        int trans = getA() + getB();
        nextEnumeration();
        return trans;
    }

    void nextEnumeration() {
        int n = enumeration.length;

        // d2 - dn. increment based on factorial radix.
        for (int i = n-1; i >= 0; i--) {
            if (enumeration[i] == i) {
                enumeration[i] = 0;
            } else {
                enumeration[i]++;
                setK(i+1);
                break;
            }
        }
    }

    int fact(int n) {
        int prod = 1;
        for (;n > 1; n--) {
            prod = prod * n;
        }
        return prod;
    }

    void swapPositions(int x, int y) {
        char temp = num[x-1];
        num[x-1] = num[y-1];
        num[y-1] = temp;
    }

    void print() {
        if (debugging) {
            for (int i = 0; i < enumeration.length; i++) {
                System.out.print(enumeration[i] + " ");
            }
        }

        System.out.println(new String(num));
    }

    void generate() {
        permMap.put(new String(num), true);
        for (int i = 0; i < fact(n) - 1; i++) {
            int pos = findNextTransposition();
            swapPositions(pos, pos+1);
            permMap.put(new String(num), true);
            print();
        }
    }

    public static void main(String args[]) {
        TestJohnsonPerm perm = new TestJohnsonPerm(5);
        perm.print();
        perm.generate();
        System.out.println("Map size is - " + perm.permMap.size());
    }
}
