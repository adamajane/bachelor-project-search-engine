package core;

import java.util.Scanner;

import static util.Config.*;

public class Driver {

    public static void main(String[] args) {

        Scanner console = new Scanner(System.in);
        String searchString;
        System.out.println("Testing index " + INDEX_TO_TEST + " with file " + FULL_FILE_PATH);
        switch (INDEX_TO_TEST) {
            case 1:
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index1 i1 = new Index1(FULL_FILE_PATH);
                for (; ; ) {
                    System.out.println("Input search string or type exit to stop");
                    searchString = console.nextLine().toLowerCase();
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
            case 2:
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index2 i2 = new Index2(FULL_FILE_PATH);
                for (; ; ) {
                    System.out.println("Input search string or type exit to stop");
                    searchString = console.nextLine().toLowerCase();
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
            case 3:
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index3 i3 = new Index3(FULL_FILE_PATH);

                while (true) { // Simple loop for multiple searches
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine().toLowerCase();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i3.search(searchString);
                }
                break;
            case 4:
                System.out.println("Preprocessing " + FULL_FILE_PATH);
                Index4 i4 = new Index4(FULL_FILE_PATH);

                while (true) {
                    System.out.println("Input search string or type 'exit' to stop");
                    searchString = console.nextLine().toLowerCase();
                    if (searchString.equals("exit")) {
                        break;
                    }
                    i4.search(searchString);
                }
                break;
        }

        console.close();
    }
}