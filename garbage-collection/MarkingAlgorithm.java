public class MarkingAlgorithm {
    public static void main(String args[]) {
        newerMemory();
    }

    static void memory() {
        Element[] memory = MemoryBuilder.buildMemory(10);
        System.out.println("Printing after creation.");
        MemoryBuilder.printMemoryContents(memory);
        // mark sources
        MemoryBuilder.markSources(memory);
        // do garbage collections.
        new MarkingVersion1a().doMarking(memory);
        // print result
        System.out.println("Printing after marking.");
        MemoryBuilder.printMemoryContents(memory);
    }

    static void newMemory() {
        NewElement[] memory = NewMemoryBuilder.buildMemory(10);
        System.out.println("Printing after creation.");
        NewMemoryBuilder.printMemoryContents(memory);
        // mark sources
        NewMemoryBuilder.markSources(memory);
        // do garbage collections.
        new MarkingVersion4(memory).doMarking();
        // print result
        System.out.println("Printing after marking.");
        NewMemoryBuilder.printMemoryContents(memory);
    }

    static void newerMemory() {
        NewerElement[] memory = NewerMemoryBuilder.buildMemory(10);
        System.out.println("Printing after creation.");
        NewerMemoryBuilder.printMemoryContents(memory);
        // mark sources
        NewerMemoryBuilder.markSources(memory);
        // do garbage collections.
        new MarkingVersion5(memory).doMarking();
        // print result
        System.out.println("Printing after marking.");
        NewerMemoryBuilder.printMemoryContents(memory);
    }
}
