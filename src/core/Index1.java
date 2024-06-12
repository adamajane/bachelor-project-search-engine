package core;

import java.io.*;
import java.util.Scanner;

public class Index1 {

    // Initial index to test the program. Checks if a word exists in the input file

    private WikiItem start;
    public long totalBytesUsed = 0;  // Global byte counter

    private class WikiItem {
        String str;
        WikiItem next;

        WikiItem(String s, WikiItem n) {
            str = s;
            next = n;
            // Update the global memory usage counter
            totalBytesUsed += estimateMemoryUsage(s) + estimateMemoryUsage(this);
        }
    }

    public Index1(String filename) {
        long startTime = System.currentTimeMillis(); // Start timing
        String word;
        WikiItem current, tmp;
        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");
            word = input.next();
            start = new WikiItem(word, null);
            current = start;
            while (input.hasNext()) {   // Read all words in input
                word = input.next();
                System.out.println(word);
                tmp = new WikiItem(word, null);
                current.next = tmp;
                current = tmp;
            }
            input.close();
            long endTime = System.currentTimeMillis(); // End timing
            long elapsedTime = endTime - startTime;
            System.out.println("Preprocessing completed in " + elapsedTime + " milliseconds.");
            System.out.println("Total memory used: " + totalBytesUsed + " bytes (" + totalBytesUsed / (1024 * 1024) + " MB).");
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }

    public boolean search(String searchstr) {
        WikiItem current = start;
        while (current != null) {
            if (current.str.equals(searchstr)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private long estimateMemoryUsage(String s) {
        int numChars = s.length();
        int memoryUsage = 8 * (int) Math.ceil(((numChars * 2) + 38) / 8.0);
        return memoryUsage;
    }

    // Helper method to estimate memory usage of a WikiItem object
    private long estimateMemoryUsage(WikiItem item) {
        return 4 + 4 + 4 + 12; // references to String, DocumentList, and next WikiItem (4 bytes each) + Object header (12 bytes)
    }
}
