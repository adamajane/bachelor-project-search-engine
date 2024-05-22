package core;

import java.io.*;
import java.util.Scanner;

import static util.Config.*;

public class Index2 {

    // Modifies Index1 to output the titles of all documents containing the specified search string

    private WikiItem start;
    private long totalBytesUsed = 0; // Global byte counter

    private class WikiItem {
        String str;
        WikiItem next;

        WikiItem(String s, WikiItem n) {
            this.str = s;
            this.next = n;

            // Estimate memory used by this WikiItem
            totalBytesUsed += estimateMemoryUsage(s);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    public Index2(String filename) {
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
        long startTime = System.currentTimeMillis(); // Start timing
        WikiItem current = start;
        boolean found = false;

        while (current != null) {
            if (current.str.endsWith(".")) {
                // Potential document title
                String documentTitle = current.str;
                StringBuilder documentContent = new StringBuilder();

                // Read the content of the document until the end marker is found
                while (current != null && !current.str.equals("---END.OF.DOCUMENT---")) {
                    documentContent.append(current.str).append(" ");
                    current = current.next;
                }

                // Check if the search string is present in the document.
                // We started using '.contains',
                // but it found all words that partially matched the search word.
                // We then changed it to using regex, such that we only find the exact word when searching,
                // and not all words that contain the word.

                if (documentContent.toString().matches("(?i).*\\b" + searchstr + "\\b.*")) {
                    System.out.println("Found in: Document Title: " + documentTitle);
                    found = true;
                }
            }
            if (current != null) {
                current = current.next;
            }
        }
        long endTime = System.currentTimeMillis(); // End timing
        long elapsedTime = endTime - startTime;
        System.out.println("Search completed in " + elapsedTime + " milliseconds.");
        return found;
    }

    // Helper method to estimate memory usage of a String object using the given formula
    private long estimateMemoryUsage(String s) {
        int numChars = s.length();
        int memoryUsage = 8 * (int) Math.ceil(((numChars * 2) + 38) / 8.0);
        return memoryUsage;
    }

    // Helper method to estimate memory usage of a WikiItem object
    private long estimateMemoryUsage(WikiItem item) {
        return 16 + 4 + 4; // Object header (16 bytes) + references to String and next WikiItem (4 bytes each)
    }
}
