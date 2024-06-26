package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Index5c {

    // This index builds upon Index5a
    // It uses an ArrayList for both the index array (for the document titles) and the document list of each WikiItem

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private ArrayList<String> documentNames;
    private int numItems = 0; // Track the number of items
    private double loadFactor = 0.75;
    public long totalBytesUsed = 0; // Global byte counter

    private class WikiItem {
        String searchString;
        ArrayList<Integer> documents;
        WikiItem next;
        int lastDocIndex; // Track the index of the last document

        WikiItem(String s, ArrayList<Integer> d, WikiItem n) {
            this.searchString = s;
            this.documents = d;
            this.next = n;
            this.lastDocIndex = -1; // Initialize to -1 to indicate no documents initially

            // Estimate memory used by this WikiItem
            totalBytesUsed += estimateMemoryUsage(s);
            totalBytesUsed += estimateMemoryUsage(this);
            // Removed the document list memory usage from here
        }
    }

    public Index5c(String filename) {
        long startTime = System.currentTimeMillis();
        hashTable = new WikiItem[tableSize];
        totalBytesUsed += estimateMemoryUsage(hashTable);
        documentNames = new ArrayList<>(); // Initialize the document names list

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
                        documentNames.add(currentTitle);
                    }
                } else {
                    if (word.equals("---END.OF.DOCUMENT---")) {
                        Scanner contentScanner = new Scanner(documentContent.toString());
                        while (contentScanner.hasNext()) {
                            addWordToIndex(contentScanner.next(), documentNames.size() - 1);
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

        // Total memory usage of the documentNames ArrayList including the strings it contains
        totalBytesUsed += estimateMemoryUsage(documentNames);

        long endTime = System.currentTimeMillis(); // End timing
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
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

    private void addWordToIndex(String word, int docId) {
        // Calculate the current load factor to determine if resizing is needed
        double currentLoadFactor = (double) (numItems + 1) / tableSize;

        // Resize the hash table if the current load factor exceeds the threshold
        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }
        int hashIndex = hash(word);
        // Find any existing WikiItem for the word in the hash table
        WikiItem existingItem = findWikiItem(word);

        // If no existing item is found, create a new one
        if (existingItem == null) {
            // Create a new document list and add the current document ID
            ArrayList<Integer> docList = new ArrayList<>();
            docList.add(docId);
            // Create a new WikiItem with the word, document list, and link it to the current hash table bucket
            WikiItem newItem = new WikiItem(word, docList, hashTable[hashIndex]);
            newItem.lastDocIndex = 0; // Since this is the first document, index is 0
            hashTable[hashIndex] = newItem; // Place the new item in the hash table
            numItems++; // Increment the count of items in the hash table
        } else {
            // If an existing item is found, add the document ID to it
            addDocumentToWikiItem(existingItem, docId);
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
                System.out.println("Rehashing item: " + item.searchString);
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
            ArrayList<Integer> docList = foundItem.documents;

            if (docList == null || docList.isEmpty()) {
                System.out.println("  No documents found.");
            } else {
                for (int docId : docList) {
                    System.out.println("  - " + documentNames.get(docId));
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
        // Retrieve the document list from the WikiItem
        ArrayList<Integer> docList = item.documents;
        // Check if the list is empty or the last document is not the current document ID
        if (item.lastDocIndex == -1 || docList.get(item.lastDocIndex) != documentId) {
            // Estimate the memory usage of the document list before adding the new document
            long oldMemoryUsage = estimateMemoryUsage(docList);

            // Add the new document ID to the document list
            docList.add(documentId);

            // Update the last document index to the new document's position
            item.lastDocIndex = docList.size() - 1;

            // Estimate the memory usage of the document list after adding the new document
            long newMemoryUsage = estimateMemoryUsage(docList);

            // Update the total memory usage with the difference in memory usage
            totalBytesUsed += (newMemoryUsage - oldMemoryUsage);
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
        long memoryUsage = 12 + 4 + 4 + 4; // Object header (12 bytes) + references to String, ArrayList, and next WikiItem (4 bytes each)
        return memoryUsage;
    }

    // Helper method to estimate memory usage of an array
    private long estimateMemoryUsage(WikiItem[] array) {
        return 12 + (array.length * 4); // Array header (12 bytes) + 4 bytes per reference
    }

    // Generic helper method to estimate memory usage of an ArrayList
    private long estimateMemoryUsage(ArrayList<?> arrayList) {
        long arrayListMemory = 12 + 4 + 4 + 4; // ArrayList object header (12 bytes) + 4 bytes each for size, modCount, and elementData array reference
        if (arrayList.size() > 0) {
            arrayListMemory += 12 + (arrayList.size() * 4); // elementData array header (12 bytes) + 4 bytes per reference
            for (Object element : arrayList) {
                if (element instanceof String) {
                    arrayListMemory += estimateMemoryUsage((String) element);
                } else if (element instanceof Integer) {
                    arrayListMemory += 4; // Integer size in bytes
                }
            }
        }
        return arrayListMemory;
    }

    public ArrayList<String> getDocumentNames() {
        return documentNames;
    }

}