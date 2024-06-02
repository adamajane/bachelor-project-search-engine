package core;

import java.io.*;
import java.util.Scanner;

public class Index2 {

    private WikiItem start;
    public long totalBytesUsed = 0;  // Global byte counter

    private class WikiItem {
        String str;
        WikiItem next;

        WikiItem(String s, WikiItem n) {
            this.str = s;
            this.next = n;
            // Update the global memory usage counter
            totalBytesUsed += estimateMemoryUsage(s) + estimateMemoryUsage(this);
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
        boolean creatingTitle = false;
        StringBuilder documentTitle = new StringBuilder();

        while (current != null) {
            // Check for potential document title
            if (!creatingTitle) {
                // Start of a new title
                documentTitle.setLength(0);  // Clear any previous title
                documentTitle.append(current.str).append(" ");
                creatingTitle = true;
            } else {
                // Continue appending to the current title
                documentTitle.append(current.str).append(" ");
            }

            // Check if the current word ends a title
            if (current.str.endsWith(".") || current.str.endsWith("!") || current.str.endsWith("?")) {
                creatingTitle = false; // End of title

                StringBuilder documentContent = new StringBuilder();

                // Move to the next item to start reading document content
                current = current.next;

                // Read the content of the document until the end marker is found
                while (current != null && !current.str.equals("---END.OF.DOCUMENT---")) {
                    documentContent.append(current.str).append(" ");
                    current = current.next;
                }

                // Check if the search string is present in the document
                String[] words = documentContent.toString().split("\\s+");
                for (String word : words) {
                    if (word.equals(searchstr)) {
                        System.out.println("Found in: Document Title: " + documentTitle.toString().trim());
                        found = true;
                        break;
                    }
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
