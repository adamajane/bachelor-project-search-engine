package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static util.Config.FULL_FILE_PATH;

public class Index6b {

    /*
    This index implements compression techniques to further reduce memory usage.

    It builds up Index6a and removes punctuation and converts all words to lowercase to further reduce memory usage.
    */

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private ArrayList<String> documentNames;
    private int numItems = 0; // Track the number of items
    private double loadFactor = 0.75;
    public long totalBytesUsed = 0; // Global byte counter
    private StringBuilder sb = new StringBuilder();

    private class WikiItem {
        String searchString;
        ByteArrayOutputStream documentDiffs;
        WikiItem next;
        int lastDocId; // Cache the last document ID

        WikiItem(String s, int firstDocId, WikiItem n) {
            this.searchString = s;
            this.documentDiffs = new ByteArrayOutputStream();
            this.lastDocId = firstDocId;
            try {
                writeVByte(firstDocId, this.documentDiffs);  // Encode the first docId
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.next = n;

            // Estimate memory used by this WikiItem
            totalBytesUsed += estimateMemoryUsage(s);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }


    private void writeVByte(int value, ByteArrayOutputStream output) throws IOException {
        ArrayList<Integer> bytes = new ArrayList<>();
        while (true) {
            bytes.add(0, value % 128); // PREPEND(bytes, n mod 128)
            if (value < 128) {
                break;
            }
            value = value / 128; // n div 128
        }
        bytes.set(bytes.size() - 1, bytes.get(bytes.size() - 1) + 128); // Modify the last element
        for (int b : bytes) {
            output.write(b);
        }
    }

    private int readVByte(ByteArrayInputStream input) {
        int n = 0;
        while (true) {
            int b = input.read();
            if (b >= 128) {
                n = 128 * n + (b - 128);
                break;
            } else {
                n = 128 * n + b;
            }
        }
        return n;
    }

    public Index6b(String filename) {
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

                    if (word.endsWith(".") || word.endsWith("!") || word.endsWith("?")) {
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
        System.out.println("Total memory used: " + totalBytesUsed + " bytes (" + totalBytesUsed / (1024 * 1024) + " MB).");
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
        // Clear the StringBuilder
        sb.setLength(0);

        // Builds a new string without alphanumeric characters and converts to lowercase
        for (char c : word.toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c)) {
                sb.append(c);
            }
        }

        word = sb.toString().toLowerCase();

        double currentLoadFactor = (double) (numItems + 1) / tableSize;

        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }

        int hashIndex = hash(word);
        WikiItem existingItem = findWikiItem(word);

        if (existingItem == null) {
            WikiItem newItem = new WikiItem(word, docId, hashTable[hashIndex]);
            hashTable[hashIndex] = newItem;
            numItems++;
        } else {
            if (existingItem.lastDocId != docId) {
                long oldMemoryUsage = estimateMemoryUsage(existingItem.documentDiffs);
                try {
                    writeVByte(docId - existingItem.lastDocId, existingItem.documentDiffs);  // Store the difference
                    existingItem.lastDocId = docId; // Update the cached lastDocId
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long newMemoryUsage = estimateMemoryUsage(existingItem.documentDiffs);
                totalBytesUsed += (newMemoryUsage - oldMemoryUsage); // Update total memory usage
            }
        }
    }

    /*
    private int decodeLastDocId(byte[] encodedDiffs) {
        ByteArrayInputStream input = new ByteArrayInputStream(encodedDiffs);
        int docId = readVByte(input);  // Decode the first docId
        while (input.available() > 0) {
            docId += readVByte(input);  // Decode and accumulate differences
        }
        return docId;
    }
    */

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
            // System.out.println("Documents associated with '" + searchString + "':");
            System.out.println("Showing results for " + searchString + ":");
            byte[] encodedDiffs = foundItem.documentDiffs.toByteArray();
            ByteArrayInputStream input = new ByteArrayInputStream(encodedDiffs);
            int docId = readVByte(input);  // Decode the first docId
            System.out.println(docId + "  - " + documentNames.get(docId));
            while (input.available() > 0) {
                docId += readVByte(input);  // Decode and accumulate differences
                System.out.println(docId + "  - " + documentNames.get(docId));
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

    //commented out because not needed when using arraylist for documents
/*
    private void addDocumentToWikiItem(WikiItem item, int documentId) {
        DocumentList currentDoc = item.documents;

        // Check if the document list is empty
        if (currentDoc == null) {
            item.documents = new DocumentList(documentId, null);
            return;  // Document added; we can return immediately
        }

        // Check the tail to avoid duplicates
        if (currentDoc.tail.documentName == documentId) {
            return; // Document already exists at the end
        }

        // Document doesn't exist yet, add it to the list
        DocumentList newDoc = new DocumentList(documentId, null);
        currentDoc.tail.next = newDoc;
        currentDoc.tail = newDoc; // Update the tail pointer
    }

 */

    /*
    public int countDocuments() {
        return documentNames.size();
    }
     */

    // Helper method to estimate memory usage of a WikiItem object
    private long estimateMemoryUsage(WikiItem item) {
        long memoryUsage = 12 + 4 + 4 + 4; // Object header (12 bytes) + references to String, ByteArrayOutputStream, and next WikiItem (4 bytes each)
        return memoryUsage;
    }

    // Helper method to estimate memory usage of a ByteArrayOutputStream object
    private long estimateMemoryUsage(ByteArrayOutputStream outputStream) {
        int size = outputStream.size();
        long memoryUsage = 12 + 4 + 4 + 4; // Object header (12 bytes) + count (4 bytes) + buf reference (4 bytes) + size reference (4 bytes)
        memoryUsage += 12 + size; // Add memory usage of the buf array (12 bytes for array header + 1 byte per element)
        return memoryUsage;
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

    // Helper method to estimate memory usage of a String object using the given formula
    private long estimateMemoryUsage(String s) {
        int numChars = s.length();
        int memoryUsage = 8 * (int) Math.ceil(((numChars * 2) + 38) / 8.0);
        return memoryUsage;
    }
}
