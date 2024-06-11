package core;

import java.io.*;
import java.util.Scanner;

import static util.Config.*;

public class Index3 {

    /* Modified the construction of the data structure to include a linked list of the possible
    search strings and the documents they appear in is constructed.
    Each object in the linked list contains three fields:
        1. The search string.
        2. A linked list of documents in which search string appears.
        3. A reference to the next item in the list.
    */

    private WikiItem index; // Represents the head of our main index
    public long totalBytesUsed = 0; // Global byte counter

    private class WikiItem {
        String searchString;
        DocumentItem documents;
        WikiItem next;

        WikiItem(String s, DocumentItem d, WikiItem n) {
            this.searchString = s;
            this.documents = d;
            this.next = n;

            // Estimate memory used by this WikiItem
            totalBytesUsed += estimateMemoryUsage(s);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    // Represents a document in which a search string appears
    private class DocumentItem {
        String documentName;
        DocumentItem next;

        DocumentItem(String documentName, DocumentItem next) {
            this.documentName = documentName;
            this.next = next;

            // Estimate memory used by this DocumentItem
            totalBytesUsed += estimateMemoryUsage(documentName);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    public Index3(String filename) {
        long startTime = System.currentTimeMillis(); // Start timing

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String currentTitle = null;
            StringBuilder documentContent = new StringBuilder();
            boolean readingTitle = true;
            String line;

            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");

                for (String word : words) {
                    if (readingTitle) {
                        if (word.endsWith(".") || word.endsWith("!") || word.endsWith("?")) {
                            currentTitle = documentContent.toString() + word;
                            readingTitle = false;
                        } else {
                            documentContent.append(word).append(" ");
                        }
                    } else {
                        if (word.equals("---END.OF.DOCUMENT---")) {
                            String content = documentContent.toString();
                            String[] contentWords = content.split("\\s+");
                            for (String w : contentWords) {
                                addWordToIndex(w, currentTitle);
                            }

                            readingTitle = true;
                            currentTitle = null;
                            documentContent.setLength(0);
                        } else {
                            documentContent.append(word).append(" ");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis(); // End timing
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
        System.out.println("Total memory used: " + totalBytesUsed + " bytes (" + totalBytesUsed / (1024 * 1024) + " MB).");
    }


    private void addWordToIndex(String word, String docTitle) {
        WikiItem existingItem = findWikiItem(word);

        // If the word doesn't exist in the index yet
        if (existingItem == null) {
            // Create a new WikiItem and start its document list
            WikiItem newItem = new WikiItem(word, new DocumentItem(docTitle, null), null);
            newItem.next = index; // Add as new head of the main index
            index = newItem;
        } else {
            // Word exists, need to add a document to its list
            addDocumentToWikiItem(existingItem, docTitle);
        }

        // Logging for debugging (remove these lines later)
        // System.out.println("Added word: " + word + " for document: " + docTitle);
    }


    public void search(String searchString) {
        long startTime = System.nanoTime(); // Start timing in nanoseconds
        WikiItem foundItem = findWikiItem(searchString);
        if (foundItem != null) {
            System.out.println("Documents associated with '" + searchString + "':");
            DocumentItem currentDoc = foundItem.documents;
            if (currentDoc == null) {
                System.out.println("  No documents found.");
            } else {
                while (currentDoc != null) {
                    System.out.println("  - " + currentDoc.documentName);
                    currentDoc = currentDoc.next;
                }
            }
        } else {
            System.out.println(searchString + " not found in the index.");
        }
        long endTime = System.nanoTime(); // End timing in nanoseconds
        long elapsedTime = endTime - startTime;
        System.out.println("Search query time: " + elapsedTime + " ns"); // Print the time taken
    }


    // Finds a WikiItem with the given searchString or returns null
    private WikiItem findWikiItem(String searchString) {
        WikiItem current = index;
        while (current != null) {
            if (current.searchString.equals(searchString)) {
                // System.out.println("Found WikiItem for: " + searchString); // Debugging print
                return current;
            }
            current = current.next;
        }
        return null; // Item not found
    }


    // Adds a document to a WikiItem's DocumentItem
    private void addDocumentToWikiItem(WikiItem item, String documentName) {
        DocumentItem currentDoc = item.documents;

        // Check for duplicates
        while (currentDoc != null) {
            if (currentDoc.documentName.equals(documentName)) {
                //System.out.println("Document '" + documentName + "' already exists in WikiItem: " + item.searchString);
                return; // Document already exists, no need to add again
            }
            currentDoc = currentDoc.next;
        }

        // If DocumentItem is empty
        if (item.documents == null) {
            item.documents = new DocumentItem(documentName, null);
        } else {
            // Adding at the end of the DocumentItem
            DocumentItem newDoc = new DocumentItem(documentName, null);
            currentDoc = item.documents; // Resetting currentDoc to the beginning

            while (currentDoc.next != null) {
                currentDoc = currentDoc.next;
            }

            currentDoc.next = newDoc;
        }

    }

    // Helper method to estimate memory usage of a String object using the given formula
    private long estimateMemoryUsage(String s) {
        int numChars = s.length();
        int memoryUsage = 8 * (int) Math.ceil(((numChars * 2) + 38) / 8.0);
        return memoryUsage;
    }

    // Helper method to estimate memory usage of a WikiItem object
    private long estimateMemoryUsage(WikiItem item) {
        return 4 + 4 + 4 + 12; //   references to String, DocumentItem, and next WikiItem (4 bytes each)+ Object header (12 bytes)
    }

    // Helper method to estimate memory usage of a DocumentItem object
    private long estimateMemoryUsage(DocumentItem item) {
        return 16 + 4 + 4; // Object header (16 bytes (with the 8-byte java object padding rule)) + references to String and next DocumentItem (4 bytes each)
    }
}
