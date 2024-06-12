package core;

import java.io.*;
import java.util.Scanner;

public class Index4 {

    // Modifies Index3 to use a hash table instead of a linked list

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private int numItems = 0; // Track the number of items
    private double loadFactor = 0.75;
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

    private class DocumentItem {
        String documentName;
        DocumentItem next;
        DocumentItem tail;

        DocumentItem(String documentName, DocumentItem next) {
            this.documentName = documentName;
            this.next = next;
            this.tail = this;

            // Estimate memory used by this DocumentItem
            totalBytesUsed += estimateMemoryUsage(documentName);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    public Index4(String filename) {
        long startTime = System.currentTimeMillis(); // Start timing
        hashTable = new WikiItem[tableSize];

        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");

            String currentTitle = null;
            StringBuilder documentContent = new StringBuilder(); // To accumulate document content
            boolean readingTitle = true; // Flag to indicate if we're reading a title

            while (input.hasNext()) {
                String word = input.next();

                if (readingTitle) {
                    if (currentTitle == null) {
                        currentTitle = word;
                    } else {
                        currentTitle = currentTitle + " " + word; // Append words to the title
                    }

                    if (word.endsWith(".") || word.endsWith("!") || word.endsWith("?")) {
                        readingTitle = false; // End of title
                    }
                } else {
                    if (word.equals("---END.OF.DOCUMENT---")) {
                        // process the document content
                        Scanner contentScanner = new Scanner(documentContent.toString());
                        while (contentScanner.hasNext()) {
                            addWordToIndex(contentScanner.next(), currentTitle);
                        }
                        readingTitle = true; // Switch back to reading title
                        currentTitle = null;
                        documentContent.setLength(0); // Clear document content
                        contentScanner.close();
                    } else {
                        documentContent.append(word).append(" ");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename); // Handle file not found exception
        }
        long endTime = System.currentTimeMillis(); // End timing
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
        totalBytesUsed += estimateMemoryUsage(hashTable); // Add the memory usage of the final hash table size
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
        System.out.println("Total memory used: " + totalBytesUsed + " bytes (" + totalBytesUsed / (1024 * 1024) + " MB).");
    }

    private int hash(String word) {
        int hashValue = word.hashCode();

        // Ensures that the hash value is non-negative
        hashValue = hashValue & 0x7fffffff;

        // Reduce the hash value to fit within your table size
        hashValue = hashValue % tableSize;

        return hashValue;
    }

    private void addWordToIndex(String word, String docTitle) {

        // Calculate the current load factor of the hash table
        double currentLoadFactor = (double) (numItems + 1) / tableSize;

        // Check if the load factor exceeds the threshold, and resize the hash table if so
        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }
        int hashIndex = hash(word);

        // Check if the word already exists in the index
        WikiItem existingItem = findWikiItem(word);

        if (existingItem == null) {
            // If not, create a new WikiItem
            WikiItem newItem = new WikiItem(word, new DocumentItem(docTitle, null), hashTable[hashIndex]);
            hashTable[hashIndex] = newItem;
            numItems++; // Increment the item count
        } else {
            // If the word exists, add the document title to its document list
            addDocumentToWikiItem(existingItem, docTitle);
        }
    }

    private int nextPrime(int input) {
        int counter;
        boolean prime = false;

        // Start searching for next prime number
        int num = input;

        while (!prime) {
            num++;
            prime = true;
            int sqrt = (int) Math.sqrt(num);

            for (counter = 2; counter <= sqrt; counter++) {
                if (num % counter == 0) {
                    prime = false;
                    break; // exit the inner for loop
                }
            }
        }

        return num;
    }

    private void resizeHashTable() {

        // Make the new table size, as a prime number
        int newTableSize = nextPrime(tableSize * 2);
        // Create a temporary table with the new size
        WikiItem[] tempTable = new WikiItem[newTableSize];

        // Iterate over the current hash table
        for (int i = 0; i < tableSize; i++) {
            WikiItem item = hashTable[i];
            while (item != null) {
                System.out.println("Rehashing item: " + item.searchString); // Log each item being rehashed
                // Compute the new index for the item in the resized table
                int newIndex = rehash(item.searchString, newTableSize);

                // Store reference to the next item
                WikiItem nextItem = item.next;

                // Insert the current item into the new table at the computed index
                item.next = tempTable[newIndex];
                tempTable[newIndex] = item;

                // Move to the next item in the old list
                item = nextItem;
            }
        }

        // Replace the old hash table with the resized one
        hashTable = tempTable;
        // Update the table size to the new size
        tableSize = newTableSize;

        System.out.println("Resize complete. New size: " + tableSize); // Log end of the resize process
    }

    // rehashes the word to fit within the new table size
    private int rehash(String word, int newSize) {
        int hashValue = word.hashCode();
        hashValue = hashValue & 0x7fffffff;
        hashValue = hashValue % newSize;
        return hashValue;
    }

    public void search(String searchString) {
        long startTime = System.nanoTime();

        // Find the WikiItem associated with the search string
        WikiItem foundItem = findWikiItem(searchString);

        long endTime = System.nanoTime();
        double timeTaken = (double) (endTime - startTime); // Convert to milliseconds

        // Print search results
        if (foundItem != null) {
            System.out.println("Documents associated with '" + searchString + "':");
            DocumentItem currentDoc = foundItem.documents;

            if (currentDoc == null) {
                System.out.println("  No documents found.");
            } else {
                // Loop through all documents associated with the WikiItem
                while (currentDoc != null) {
                    System.out.println("  - " + currentDoc.documentName);
                    currentDoc = currentDoc.next;
                }
            }
        } else {
            System.out.println(searchString + " not found in the index.");
        }

        System.out.println("Search query time: " + timeTaken + " ns");
    }

    private WikiItem findWikiItem(String searchString) {
        // Hash the search string to get an index in the hash table
        int hashIndex = hash(searchString);
        WikiItem current = hashTable[hashIndex];

        // Traverse the linked list at the hash table index
        while (current != null) {
            if (current.searchString.equals(searchString)) {
                return current; // Found the WikiItem
            }
            current = current.next;
        }

        return null; // Search string not found in the hash table
    }

    private void addDocumentToWikiItem(WikiItem item, String documentName) {
        DocumentItem currentDoc = item.documents;

        // Check if the document list is empty
        if (currentDoc == null) {
            item.documents = new DocumentItem(documentName, null);
            return;  // Document added; we can return immediately
        }

        // Check the tail to avoid duplicates
        if (currentDoc.tail.documentName.equals(documentName)) {
            return; // Document already exists at the end
        }

        // The document doesn't exist yet, add it to the list
        DocumentItem newDoc = new DocumentItem(documentName, null);
        currentDoc.tail.next = newDoc;
        currentDoc.tail = newDoc; // Update the tail pointer
    }

    // Helper method to estimate memory usage of a String object using the given formula
    private long estimateMemoryUsage(String s) {
        int numChars = s.length();
        int memoryUsage = 8 * (int) Math.ceil(((numChars * 2) + 38) / 8.0);
        return memoryUsage;
    }

    // Helper method to estimate memory usage of a WikiItem object
    private long estimateMemoryUsage(WikiItem item) {
        return 12 + 4 + 4 + 4; // Object header (12 bytes) + references to String, DocumentItem, and next WikiItem (4 bytes each)
    }

    // Helper method to estimate memory usage of a DocumentItem object
    private long estimateMemoryUsage(DocumentItem item) {
        return 12 + 4 + 4 + 4; // Object header (12 bytes) + references to String, next and tail DocumentItem (4 bytes each)
    }

    // Helper method to estimate memory usage of an array
    private long estimateMemoryUsage(WikiItem[] array) {
        return 12 + (array.length * 4); // Array header (12 bytes) + 4 bytes per reference
    }
}
