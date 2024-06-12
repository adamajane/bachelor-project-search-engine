package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Index6a {

    /*
    This index implements compression techniques to further reduce memory usage.

    It builds up Index5a, which uses an ArrayList for the index array.

    In addition, it uses a difference array and variable byte encoding for storing document IDs to make the
    index more compact.
    */
    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private ArrayList<String> documentNames;
    private int numItems = 0;
    private double loadFactor = 0.75;
    public long totalBytesUsed = 0;

    private class WikiItem {
        String searchString;
        ByteArrayOutputStream documentDiffs; // Stream to store document ID differences using variable-byte encoding.
        WikiItem next;
        int lastDocId; // Cache the last document ID where the search word was found.

        WikiItem(String s, int firstDocId, WikiItem n) {
            this.searchString = s;
            this.documentDiffs = new ByteArrayOutputStream();
            this.lastDocId = firstDocId;

            // Write the first document ID to the document differences stream.
            try {
                writeVByte(firstDocId, this.documentDiffs);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.next = n; // Set the next item in the linked list.

            // Update the total memory usage to account for the new item.
            totalBytesUsed += estimateMemoryUsage(s);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    private void writeVByte(int value, ByteArrayOutputStream output) throws IOException {
        // List to store the bytes of the encoded value.
        ArrayList<Integer> bytes = new ArrayList<>();

        // Loop to encode the integer value into bytes.
        while (true) {
            // Prepend the least significant 7 bits of the value to the bytes list.
            bytes.add(0, value % 128);
            // Check if the value fits within 7 bits.
            if (value < 128) {
                break;
            }
            // Shift right by 7 bits (divide by 128) to process the next 7 bits.
            value = value / 128;
        }

        // Modify the last byte to mark the end of the encoded value.
        bytes.set(bytes.size() - 1, bytes.get(bytes.size() - 1) + 128);

        // Write all the bytes to the output stream.
        for (int b : bytes) {
            output.write(b);
        }
    }

    private int readVByte(ByteArrayInputStream input) {
        int n = 0;

        // Loop to decode the bytes from the input stream.
        while (true) {
            // Read a byte from the input stream.
            int b = input.read();
            // Check if this is the last byte of the encoded value.
            if (b >= 128) {
                // Add the value of this byte to the integer, removing the end marker.
                n = 128 * n + (b - 128);
                break;
            } else {
                // Add the value of this byte to the integer.
                n = 128 * n + b;
            }
        }

        // Return the decoded integer value.
        return n;
    }

    public Index6a(String filename) {
        long startTime = System.currentTimeMillis();
        hashTable = new WikiItem[tableSize];
        totalBytesUsed += estimateMemoryUsage(hashTable);
        documentNames = new ArrayList<>();

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
                        currentTitle = currentTitle + " " + word;
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

        long endTime = System.currentTimeMillis();
        double minutes = (double) (endTime - startTime) / (1000 * 60);
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
        System.out.println("Total memory used: " + totalBytesUsed + " bytes (" + totalBytesUsed / (1024 * 1024) + " MB).");
    }

    private int hash(String word) {
        int hashValue = word.hashCode();
        hashValue = hashValue & 0x7fffffff;
        hashValue = hashValue % tableSize;
        return hashValue;
    }

    private void addWordToIndex(String word, int docId) {
        double currentLoadFactor = (double) (numItems + 1) / tableSize;

        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }

        int hashIndex = hash(word);

        // Search for an existing item with the same word.
        WikiItem existingItem = findWikiItem(word);

        // If the word is not already in the hash table, add it as a new item.
        if (existingItem == null) {
            // Create a new WikiItem and add it to the hash table at the computed index.
            WikiItem newItem = new WikiItem(word, docId, hashTable[hashIndex]);
            hashTable[hashIndex] = newItem;
            numItems++; // Increment the number of items in the hash table.
        } else {
            // If the current document doesnt already contain the word, add the documentdifference to the bytearray.
            if (existingItem.lastDocId != docId) {
                // Estimate the current memory usage of the document differences.
                long oldMemoryUsage = estimateMemoryUsage(existingItem.documentDiffs);
                try {
                    // Write the difference between the current and last document IDs.
                    writeVByte(docId - existingItem.lastDocId, existingItem.documentDiffs);
                    // Update the last document ID to the current one.
                    existingItem.lastDocId = docId;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Estimate the new memory usage after writing the difference.
                long newMemoryUsage = estimateMemoryUsage(existingItem.documentDiffs);
                // Update the total memory usage with the difference.
                totalBytesUsed += (newMemoryUsage - oldMemoryUsage);
            }
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
                System.out.println("Rehashing item: " + item.searchString);
                int newIndex = rehash(item.searchString, newTableSize);

                WikiItem nextItem = item.next;

                item.next = tempTable[newIndex];
                tempTable[newIndex] = item;

                item = nextItem;
            }
        }

        hashTable = tempTable;
        tableSize = newTableSize;
        totalBytesUsed += estimateMemoryUsage(tempTable);

        System.out.println("Resize complete. New size: " + tableSize);
    }

    private int rehash(String word, int newSize) {
        int hashValue = word.hashCode();
        hashValue = hashValue & 0x7fffffff;
        hashValue = hashValue % newSize;
        return hashValue;
    }

    public void search(String searchString) {
        WikiItem foundItem = findWikiItem(searchString);

        // If the item is found in the index.
        if (foundItem != null) {
            System.out.println("Documents associated with '" + searchString + "':");

            // Get the byte array of encoded document ID differences.
            byte[] encodedDiffs = foundItem.documentDiffs.toByteArray();
            // Create a ByteArrayInputStream to read the encoded differences.
            ByteArrayInputStream input = new ByteArrayInputStream(encodedDiffs);

            // Read and decode the first document ID.
            int docId = readVByte(input);
            // Print the document ID and its corresponding name.
            System.out.println(docId + "  - " + documentNames.get(docId));

            // Continue reading and decoding the remaining document ID differences.
            while (input.available() > 0) {
                // Read the next document ID difference and add it to the previous document ID.
                docId += readVByte(input);
                // Print the document ID and its corresponding name.
                System.out.println(docId + "  - " + documentNames.get(docId));
            }
        } else {
            // If the item is not found in the index.
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

        return null;
    }

    private long estimateMemoryUsage(WikiItem item) {
        long memoryUsage = 12 + 4 + 4 + 4;
        return memoryUsage;
    }

    private long estimateMemoryUsage(ByteArrayOutputStream outputStream) {
        int size = outputStream.size();
        long memoryUsage = 12 + 4 + 4 + 4;
        memoryUsage += 12 + size;
        return memoryUsage;
    }

    private long estimateMemoryUsage(WikiItem[] array) {
        return 12 + (array.length * 4);
    }

    private long estimateMemoryUsage(ArrayList<String> arrayList) {
        long arrayListMemory = 12 + 4 + 4 + 4;
        if (arrayList.size() > 0) {
            arrayListMemory += 12 + (arrayList.size() * 4);
            for (String s : arrayList) {
                arrayListMemory += estimateMemoryUsage(s);
            }
        }
        return arrayListMemory;
    }

    private long estimateMemoryUsage(String s) {
        int numChars = s.length();
        int memoryUsage = 8 * (int) Math.ceil(((numChars * 2) + 38) / 8.0);
        return memoryUsage;
    }
}
