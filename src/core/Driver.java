package core;

import java.util.Scanner;

import static util.Config.*;

public class Driver {

    public static void main(String[] args) {

        Scanner console = new Scanner(System.in);
        String searchString;
        long heapSize;
        System.out.println("Testing index " + INDEX_TO_TEST + " with file " + FULL_FILE_PATH);
        switch (INDEX_TO_TEST) {
            case "1":
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index1 i1 = new Index1(FULL_FILE_PATH);
                for (; ; ) {
                    System.out.println("Input search string or type exit to stop");
                    searchString = console.nextLine();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    if (i1.search(searchString)) {
                        System.out.println(searchString + " exists");
                    } else {
                        System.out.println(searchString + " does not exist");
                    }
                }
                break;
            case "2":
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index2 i2 = new Index2(FULL_FILE_PATH);
                System.out.println("Current heap size: " + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");

                while (true) {
                    System.out.println("Input search string or type exit to stop");
                    searchString = console.nextLine();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    if (i2.search(searchString)) {
                        System.out.println(searchString + " exists");
                    } else {
                        System.out.println(searchString + " does not exist");
                    }
                }
                break;
            case "3":
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index3 i3 = new Index3(FULL_FILE_PATH);

                while (true) { // Simple loop for multiple searches
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i3.search(searchString);
                }
                break;
            case "4":
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index4 i4 = new Index4(FULL_FILE_PATH);

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i4.search(searchString);
                }
                break;
            case "5a":
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index5a i5a = new Index5a(FULL_FILE_PATH);

                System.out.println("Total memory used: " + i5a.getTotalBytesUsed() + " bytes (" + i5a.getTotalBytesUsed() / (1024 * 1024) + " MB).");
                System.out.println("Number of articles: " + i5a.getDocumentNames().size());

                heapSize = Runtime.getRuntime().totalMemory();
                System.out.println("Current heap size: " + heapSize / (1024 * 1024) + " MB");

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i5a.search(searchString);
                }
                break;
            case "5b":
                // long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index5b i5b = new Index5b(FULL_FILE_PATH);

                // long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                // System.out.println("Memory Used: " + (afterUsedMem - beforeUsedMem));

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i5b.search(searchString);
                }
                break;
            case "5c":
                // long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index5c i5c = new Index5c(FULL_FILE_PATH);

                // long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                // System.out.println("Memory Used: " + (afterUsedMem - beforeUsedMem));

                System.out.println("Number of articles: " + i5c.getDocumentNames().size());

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine().toLowerCase();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i5c.search(searchString);
                }
                break;
            case "5d":
                // long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index5d i5d = new Index5d(FULL_FILE_PATH);

                System.out.println("Total memory used: " + i5d.getTotalBytesUsed() + " bytes (" + i5d.getTotalBytesUsed() / (1024 * 1024) + " MB).");
                System.out.println("Number of articles: " + i5d.getDocumentNames().size());

                System.out.println("Number of articles: " + i5d.getDocumentNames().size());

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine().toLowerCase();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i5d.search(searchString);
                }
                break;
            case "6a":
                //long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index6a i6a = new Index6a(FULL_FILE_PATH);

                //long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                //System.out.println("Memory Used:" + (afterUsedMem-beforeUsedMem));
                //System.out.println(index.countDocuments());

                heapSize = Runtime.getRuntime().totalMemory();
                System.out.println("Current heap size: " + heapSize / (1024 * 1024) + " MB");

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i6a.search(searchString);
                }
                break;
            case "6b":
                // long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index6b i6b = new Index6b(FULL_FILE_PATH);

                // long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                // System.out.println("Memory Used:" + (afterUsedMem-beforeUsedMem));
                // System.out.println(index.countDocuments());

                heapSize = Runtime.getRuntime().totalMemory();
                System.out.println("Current heap size: " + heapSize / (1024 * 1024) + " MB");

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine().toLowerCase().replaceAll("\\s", "").replaceAll("\\p{Punct}", "");
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i6b.search(searchString);
                }
                break;
        }

        console.close();
    }
}