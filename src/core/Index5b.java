package core;

import java.io.*;
import java.util.Scanner;

public class Index5b {

    // This index modifies Index5a to use a string array for the index array for the document titles

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private String[] documentNames;
    private int documentNamesSize = 0; // Track the number of document names
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

    public Index5b(String filename) {
        long startTime = System.currentTimeMillis(); // Start timing
        hashTable = new WikiItem[tableSize];
        totalBytesUsed += estimateMemoryUsage(hashTable);
        documentNames = new String[100]; // Initialize the document names array with an initial capacity

        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");

            String currentTitle = null;
            StringBuilder documentContent = new StringBuilder();
            boolean readingTitle = true;

            while (input.hasNext()) {
                String word = input.next();

                if (readingTitle) {
                    if (currentTitle == null) {
                        currentTitle = word;
                    } else {
                        currentTitle = currentTitle + " " + word; // Append words
                    }

                    if (word.endsWith(".")) {
                        readingTitle = false;
                        addDocumentName(currentTitle);
                    }
                } else {
                    if (word.equals("---END.OF.DOCUMENT---")) {
                        Scanner contentScanner = new Scanner(documentContent.toString());
                        while (contentScanner.hasNext()) {
                            addWordToIndex(contentScanner.next(), documentNamesSize - 1);
                        }
                        readingTitle = true;
                        currentTitle = null;
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

        // Total memory usage of the documentNames array including the strings it contains
        totalBytesUsed += estimateMemoryUsage(documentNames);

        long endTime = System.currentTimeMillis(); // End timing
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
    }

    private void addDocumentName(String documentName) {
        // Check if the array is full and needs resizing
        if (documentNamesSize == documentNames.length) {
            resizeDocumentNames();
        }
        // Add the new document name to the array
        documentNames[documentNamesSize] = documentName;
        // Increment the size counter
        documentNamesSize++;
    }

    private void resizeDocumentNames() {
        // Create a new array with double the length of the current array
        String[] newDocumentNames = new String[documentNames.length * 2];
        // Copy the contents of the current array to the new array
        System.arraycopy(documentNames, 0, newDocumentNames, 0, documentNames.length);
        // Update the reference to point to the new array
        documentNames = newDocumentNames;
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

        double currentLoadFactor = (double) (numItems + 1) / tableSize;

        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }

        int hashIndex = hash(word);
        WikiItem existingItem = findWikiItem(word);

        if (existingItem == null) {
            WikiItem newItem = new WikiItem(word, new DocumentItem(docId, null), hashTable[hashIndex]);
            hashTable[hashIndex] = newItem;
            numItems++; // Increment the item count
        } else {
            addDocumentToWikiItem(existingItem, docId);
        }
    }

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
                    break;
                }
            }
        }

        return num;
    }

    private void resizeHashTable() {
        System.out.println("Starting resize...");

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

        totalBytesUsed += estimateMemoryUsage(tempTable);

        System.out.println("Resize complete. New size: " + tableSize); // Log end
    }

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
                    System.out.println("  - " + documentNames[currentDoc.documentName]);
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

    // Helper method to estimate memory usage of a String array
    private long estimateMemoryUsage(String[] array) {
        long memoryUsage = 12 + (array.length * 4); // Array header (12 bytes) + 4 bytes per reference
        for (String s : array) {
            if (s != null) {
                memoryUsage += estimateMemoryUsage(s);
            }
        }
        return memoryUsage;
    }

    // Helper method to estimate memory usage of a WikiItem array
    private long estimateMemoryUsage(WikiItem[] array) {
        return 12 + (array.length * 4); // Array header (12 bytes) + 4 bytes per reference
    }


    public String[] getDocumentNames() {
        return documentNames;
    }
}
