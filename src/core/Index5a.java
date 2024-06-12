package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Index5a {

    /* This index implements space efficiency features.
    It modifies Index4 to use a separate array for the article titles in the linked list of documents
    instead of the string name.

    In this index, an ArrayList is used for the separate array for the document titles
    */

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private ArrayList<String> documentNames; // separate array for document titles
    private int numItems = 0; // Track the number of items
    private double loadFactor = 0.75;
    private long totalBytesUsed = 0; // Global byte counter

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
        int documentName;
        DocumentItem next;
        DocumentItem tail;

        DocumentItem(int documentName, DocumentItem next) {
            this.documentName = documentName;
            this.next = next;
            this.tail = this;

            // Estimate memory used by this DocumentItem
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    public Index5a(String filename) {
        // Start measuring time to build the search index
        long startTime = System.currentTimeMillis();

        // Initialize the hash table and document title array
        hashTable = new WikiItem[tableSize];
        documentNames = new ArrayList<>();

        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");

            String currentTitle = null;
            StringBuilder documentContent = new StringBuilder();
            boolean readingTitle = true;

            while (input.hasNext()) {
                String word = input.next();

                // Extract and store document titles
                if (readingTitle) {
                    if (currentTitle == null) {
                        currentTitle = word;
                    } else {
                        currentTitle = currentTitle + " " + word; // Append words
                    }

                    if (word.endsWith(".")) {
                        readingTitle = false;
                        documentNames.add(currentTitle); // Add title to the array
                        currentTitle = null;
                    }
                } else {
                    // Process document content and add words to the index
                    if (word.equals("---END.OF.DOCUMENT---")) {
                        Scanner contentScanner = new Scanner(documentContent.toString());
                        while (contentScanner.hasNext()) {
                            // Store the index of the document title for this word
                            addWordToIndex(contentScanner.next(), documentNames.size() - 1);
                        }
                        readingTitle = true;
                        documentContent.setLength(0);
                        contentScanner.close();
                    } else {
                        documentContent.append(word).append(" ");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }

        // Account for memory used by the data structures
        totalBytesUsed += estimateMemoryUsage(documentNames);
        totalBytesUsed += estimateMemoryUsage(hashTable);

        // End measuring time to build the search index
        long endTime = System.currentTimeMillis();
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
    }

    private int hash(String word) {
        int hashValue = word.hashCode();

        // Ensures that the hash value is non-negative
        hashValue = hashValue & 0x7fffffff;

        // Reduce the hash value to fit within your table size
        hashValue = hashValue % tableSize;

        return hashValue;
    }

    private void addWordToIndex(String word, int docId) {
        // Check if the load factor is too high, and resize if so
        double currentLoadFactor = (double) (numItems + 1) / tableSize;
        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }
        int hashIndex = hash(word);

        // Find an existing WikiItem for this word in the hash table
        WikiItem existingItem = findWikiItem(word);

        // If the word is not yet in the index, create a new WikiItem for it
        if (existingItem == null) {
            // Create a new DocumentItem for the document and link it to the WikiItem
            WikiItem newItem = new WikiItem(word, new DocumentItem(docId, null), hashTable[hashIndex]);
            // Add the new WikiItem to the head of the collision list at the hash table index
            hashTable[hashIndex] = newItem;
            numItems++; // Increment the count of items in the index
        } else {
            // The word already exists in the index, add this document to the existing WikiItem
            addDocumentToWikiItem(existingItem, docId);
        }
    }

    // Helper method to find the next prime number
    private int nextPrime(int input) {
        int counter;
        boolean prime = false;

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

    // Helper method to resize the hash table
    private void resizeHashTable() {
        System.out.println("Starting resize..."); // Log start

        int newTableSize = nextPrime(tableSize * 2);
        WikiItem[] tempTable = new WikiItem[newTableSize];

        for (int i = 0; i < tableSize; i++) {
            WikiItem item = hashTable[i];
            while (item != null) {
                System.out.println("Rehashing item: " + item.searchString); // Log item
                int newIndex = rehash(item.searchString, newTableSize);

                WikiItem nextItem = item.next; // Save the next item

                // Insert at the head of the list in the new table
                item.next = tempTable[newIndex];
                tempTable[newIndex] = item;

                item = nextItem; // Move to the next item in the old list
            }
        }

        hashTable = tempTable;
        tableSize = newTableSize;

        System.out.println("Resize complete. New size: " + tableSize); // Log end
    }

    // Helper method to rehash an item
    private int rehash(String word, int newSize) {
        int hashValue = word.hashCode();
        hashValue = hashValue & 0x7fffffff;
        hashValue = hashValue % newSize;
        return hashValue;
    }

    public void search(String searchString) {
        WikiItem foundItem = findWikiItem(searchString);

        if (foundItem != null) {
            System.out.println("Documents associated with '" + searchString + "':");
            DocumentItem currentDoc = foundItem.documents;

            if (currentDoc == null) {
                System.out.println("  No documents found.");
            } else {
                while (currentDoc != null) {
                    // Retrieve the document title from the array
                    System.out.println("  - " + documentNames.get(currentDoc.documentName));
                    currentDoc = currentDoc.next;
                }
            }
        } else {
            System.out.println(searchString + " not found in the index.");
        }
    }

    private WikiItem findWikiItem(String searchString) {
        int hashIndex = hash(searchString);
        WikiItem current = hashTable[hashIndex];

        while (current != null) {
            if (current.searchString.equals(searchString)) {
                //System.out.println("Found WikiItem for: " + searchString);
                return current;
            }
            current = current.next;
        }

        return null; // Item not found
    }

    private void addDocumentToWikiItem(WikiItem item, int documentId) {
        DocumentItem currentDoc = item.documents;

        // Check if the document list is empty
        if (currentDoc == null) {
            item.documents = new DocumentItem(documentId, null);
            return;  // Document added; we can return immediately
        }

        // Check the tail to avoid duplicates
        if (currentDoc.tail.documentName == documentId) {
            return; // Document already exists at the end
        }

        // Document doesn't exist yet, add it to the list
        DocumentItem newDoc = new DocumentItem(documentId, null);
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
        return 12 + 4 + 4 + 4; // Object header (12 bytes) + int (4 bytes) + references to next DocumentItem and tail DocumentItem (4 bytes each)
    }

    // Helper method to estimate memory usage of an array
    private long estimateMemoryUsage(WikiItem[] array) {
        return 12 + (array.length * 4); // Array header (12 bytes) + 4 bytes per reference
    }

    // Helper method to estimate memory usage of an ArrayList
    private long estimateMemoryUsage(ArrayList<String> arrayList) {
        long arrayListMemory = 12 + 4 + 4 + 4; // ArrayList object header (12 bytes) + 4 bytes each for size, modCount, and elementData array reference
        if (arrayList.size() > 0) {
            arrayListMemory += 12 + (arrayList.size() * 4); // elementData array header (12 bytes) + 4 bytes per reference
            for (String s : arrayList) {
                arrayListMemory += estimateMemoryUsage(s);
            }
        }
        return arrayListMemory;
    }

    public ArrayList<String> getDocumentNames() {
        return documentNames;
    }

}
