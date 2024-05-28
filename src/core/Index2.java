package core;

import java.io.*;
import java.util.Scanner;

public class Index2 {

    // Modifies Index1 to output the titles of all documents containing the specified search string

    private WikiItem start;

    private class WikiItem {
        String str;
        WikiItem next;

        WikiItem(String s, WikiItem n) {
            this.str = s;
            this.next = n;
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
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }

    public boolean search(String searchstr) {
        long startTime = System.currentTimeMillis(); // Start timing
        WikiItem current = start;
        boolean found = false;

        while (current != null) {
            if (current.str.endsWith(".") || current.str.endsWith("!") || current.str.endsWith("?")) {
                // Potential document title
                String documentTitle = current.str;
                StringBuilder documentContent = new StringBuilder();

                // Read the content of the document until the end marker is found
                while (current != null && !current.str.equals("---END.OF.DOCUMENT---")) {
                    documentContent.append(current.str).append(" ");
                    current = current.next;
                }

                // Check if the search string is present in the document
                if (documentContent.indexOf(searchstr) != -1) {
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
}
