package core;

import java.io.*;
import java.util.Scanner;

class Index2 {

    WikiItem start;

    private class WikiItem {
        String str;
        WikiItem next;

        WikiItem(String s, WikiItem n) {
            str = s;
            next = n;
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
            if (current.str.endsWith(".")) {
                // Potential document title
                String documentTitle = current.str;
                StringBuilder documentContent = new StringBuilder();

                // Read the content of the document until the end marker is found
                while (current != null && !current.str.equals("---END.OF.DOCUMENT---")) {
                    documentContent.append(current.str).append(" ");
                    current = current.next;
                }

                // Check if the search string is present in the document. We started of using '.contains', but it found all words that partially matched the search word.
                // we then changed it to using regex, such that we only find the exact word when searching, and not all words that contain the word.

                if (documentContent.toString().matches(".*\\b" + searchstr + "\\b.*")) {
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
        System.out.println("Preprocessing completed in " + elapsedTime + " milliseconds.");
        return found;
    }

    public static void main(String[] args) {
        // Specify the file path
        //String filePath = "C:\\Users\\olski\\Desktop\\WestburyLab.wikicorp.201004_1MB.txt";
        String filePath = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/WestburyLab.wikicorp.201004_20MB.txt";
        //String filePath = "/Users/Adam/IdeaProjects/bachelor-project-search-engine/data-files/WestburyLab.wikicorp.201004_100KB.txt";

        System.out.println("Preprocessing " + filePath);
        Index2 i = new Index2(filePath);
        Scanner console = new Scanner(System.in);
        for (; ; ) {
            System.out.println("Input search string or type exit to stop");
            String searchstr = console.nextLine();
            if (searchstr.equals("exit")) {
                break;
            }
            if (i.search(searchstr)) {
                System.out.println(searchstr + " exists");
            } else {
                System.out.println(searchstr + " does not exist");
            }
        }
        console.close();
    }
}