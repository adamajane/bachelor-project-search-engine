package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static util.Config.*;

public class Index5d {

    /* This index implements space efficiency features.
    It modifies Index4 to use an index array for the article titles in the linked list of documents
    instead of the string name.

    In this index (Index5a), an ArrayList is used for the index array and for the document list.
    */

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private ArrayList<String> documentNames;
    private int numItems = 0; // Track the number of items
    private double loadFactor = 0.75;
    private long totalBytesUsed = 0; // Global byte counter

    private class WikiItem {
        String searchString;
        ArrayList<Integer> documents;
        int lastDocIndex; // Track the index of the last document
        WikiItem next;

        WikiItem(String s, ArrayList<Integer> d, WikiItem n) {
            this.searchString = s;
            this.documents = d;
            this.next = n;
            this.lastDocIndex = -1; // Initialize to -1 to indicate no documents initially

            // Estimate memory used by this WikiItem
            totalBytesUsed += estimateMemoryUsage(s);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    public Index5d(String filename) {
        long startTime = System.currentTimeMillis(); // Start timing
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
                        totalBytesUsed += estimateMemoryUsage(currentTitle);
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

        totalBytesUsed += estimateMemoryUsage(documentNames);

        long endTime = System.currentTimeMillis(); // End timing
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
    }

    // Using modulus instead of logical AND, reduced the running time by half!!
    // Using java inbuilt hash function on strings now further increased runtime by 20-25%
    private int hash(String word) {
        // Use the built-in hashCode() method
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
            ArrayList<Integer> docList = new ArrayList<>();
            docList.add(docId);
            WikiItem newItem = new WikiItem(word, docList, hashTable[hashIndex]);
            newItem.lastDocIndex = 0; // Since this is the first document, index is 0
            hashTable[hashIndex] = newItem;
            numItems++; // Increment the item count
        } else {
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
                    break; // exit the inner for loop
                }
            }
        }

        return num;
    }

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
                //System.out.println("Found WikiItem for: " + searchString);
                return current;
            }
            current = current.next;
        }

        return null; // Item not found
    }

    private void addDocumentToWikiItem(WikiItem item, int documentId) {
        ArrayList<Integer> docList = item.documents;
        if (item.lastDocIndex == -1 || docList.get(item.lastDocIndex) != documentId) {
            docList.add(documentId);
            item.lastDocIndex = docList.size() - 1; // Update the last document index
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
        memoryUsage += estimateMemoryUsage(item.documents); // Add memory usage of the ArrayList
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

    public long getTotalBytesUsed() {
        return totalBytesUsed;
    }
}
